package app.controllers;

import app.cores.StageManager;
import app.dtos.OrderDto;
import app.entities.BarTable;
import app.entities.Category;
import app.entities.Product;
import app.entities.User;
import app.enums.ViewElementPath;
import app.enums.ViewPath;
import app.services.api.BarTableService;
import app.services.api.CategoryService;
import app.services.api.OrderService;
import app.services.api.ProductService;
import app.spring.config.SpringFXMLLoader;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class SaleController implements FxmlController {

    private final Image ORDERS_BUTTON_INACTIVE_IMAGE = new Image("static_data/images/ordersButton.png");
    private final Image ORDERS_BUTTON_ACTIVE_IMAGE = new Image("static_data/images/ordersButtonActive.png");
    private final Image TABLES_BUTTON_INACTIVE_IMAGE = new Image("static_data/images/tableButton.png");
    private final Image TABLES_BUTTON_ACTIVE_IMAGE = new Image("static_data/images/tableButtonActive.png");

    private final int TABLES_GRID_COLUMNS = 3;
    private final int PRODUCT_CATEGORY_GRID_COLUMNS = 4;

    @FXML
    public Button logoutButton, managerButton, incrementQuantityButton, decrementQuantityButton;
    @FXML
    public ImageView tablesButtonImage, ordersButtonImage, managerButtonImage;
    @FXML
    private Label currentTimeLabel, currentUserLabel, selectedTableNumber, productLabel, productQuantityLabel,
            productPriceLabel, productTotalSumLabel, productCountLabel, totalSumLabel, totalTaxLabel,
            payViewCash, payViewTotalSum, payViewTax, payViewChange, invalidInputLabel, emptyCartLabel;
    @FXML
    private GridPane tableGridPane, productGridPane, categoryGridPane, payViewGridPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private HBox hyperlinkHBox;
    @FXML
    private TableView<Map.Entry<Product, Integer>> cartTableView;
    @FXML
    private TableColumn<Map.Entry<Product, Integer>, String> productColumn, quantityColumn, priceColumn, totalSumColumn;
    @FXML
    private BorderPane contentPane;
    @FXML
    private VBox mainMenuPane;

    private StageManager stageManager;
    private User currentUser;
    private List<BarTable> barTableList;
    private OrderService orderService;
    private ToggleGroup toggleGroup;
    private OrderDto orderDto;
    private ToggleButton lastToggledTableButton;
    private CategoryService categoryService;
    private ProductService productService;
    private List<Category> categoryList;
    private Product selectedProduct;
    private SpringFXMLLoader springFXMLLoader;
    private Thread cartEmptyWarningThread;

    @Autowired
    @Lazy
    public SaleController(StageManager stageManager, BarTableService barTableService,
                          OrderService orderService, CategoryService categoryService,
                          ProductService productService, SpringFXMLLoader springFXMLLoader) {
        this.stageManager = stageManager;
        this.barTableList = barTableService.getAllBarTables();
        this.orderService = orderService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.springFXMLLoader = springFXMLLoader;
    }

    @Override
    public void initialize() {

        this.categoryList = this.categoryService.getAllCategories();
        this.currentUser = this.stageManager.getUser();

        //TODO Remove before building
        // Init Dev
        //this.cartTableView.setItems(initOrder());
        //initUserDev();

        if (this.currentUser != null) {
            //gets UserName next to clock
            this.currentUserLabel.setText(this.currentUser.getName());

            //Hide managerButton if user is not admin
            if (!"admin".equalsIgnoreCase(this.currentUser.getRole())) {
                this.managerButton.setDisable(true);
                this.managerButtonImage.setOpacity(0);
            }
        }

        //making parent of GridPane transparent
        this.scrollPane.setStyle("-fx-background: transparent;" +
                "-fx-background-color: transparent;");

        //ToggleButtons act like RadioButtons
        this.toggleGroup = new ToggleGroup();
        this.toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                toggleGroup.selectToggle(oldValue);
            } else {
                //Fill cart with products when tables is selected
                this.lastToggledTableButton = (ToggleButton) newValue;
                BarTable barTable = (BarTable) this.lastToggledTableButton.getUserData();
                this.orderDto = this.orderService.findOpenOrderByTable(barTable.getId());

                this.selectedProduct = null;
                this.productLabel.setText("");
                this.productQuantityLabel.setText("0");
                this.productPriceLabel.setText("$0.00");

                //Loads Order or creates new one
                if (this.orderDto == null) {
                    this.orderDto = new OrderDto();
                    this.orderDto.setUser(this.currentUser);
                    this.orderDto.setBarTable(barTable);
                    this.cartTableView.getItems().clear();
                    this.productCountLabel.setText("0");
                } else {
                    ObservableList<Map.Entry<Product, Integer>> observableList = FXCollections.observableArrayList();
                    observableList.addAll(this.orderDto.getProducts().entrySet());
                    this.cartTableView.setItems(observableList);
                    this.productCountLabel.setText(String.valueOf(this.cartTableView.getItems().size()));
                }
                calculateSumLabels();
            }
        });

        //sets properties of cartTableView
        initiateCart();

        //clock
        timeInfo();

        //Create and fill GridPane with all categories
        createCategoryGrid();
        fillCategoryGrid();

        //Create and fill GridPane with current tables
        createTablesGrid();
        fillTablesGrid();

        //Controller starts with view of Tables
        tablesButtonHandler();
    }

    @FXML
    private void logout() {
        this.stageManager.switchScene(ViewPath.LOGIN);
    }

    @FXML
    private void managerButtonHandler() {
        this.stageManager.switchScene(ViewPath.MANAGER);
    }

    @FXML
    private void ordersButtonHandler() {
        //change background of buttons
        this.tablesButtonImage.setImage(TABLES_BUTTON_INACTIVE_IMAGE);
        this.ordersButtonImage.setImage(ORDERS_BUTTON_ACTIVE_IMAGE);

        //add hyperlink
        this.hyperlinkHBox.getChildren().clear();
        Hyperlink ordersHyperlink = new Hyperlink("ORDERS");
        ordersHyperlink.getStyleClass().add("topNavigationHyperlink");
        ordersHyperlink.setOnAction(e -> ordersButtonHandler());
        this.hyperlinkHBox.getChildren().add(ordersHyperlink);

        //check if table is selected
        if (this.toggleGroup.getSelectedToggle() == null) {
            //Shows message if table is not selected
            Label label = new Label("SELECT A TABLE");
            label.setStyle("-fx-font-size: 40px;" +
                    "-fx-alignment: center;");
            label.setPrefSize(this.scrollPane.getPrefWidth() - 10,
                    this.scrollPane.getPrefHeight() - 10);
            this.scrollPane.setContent(label);
        }
        //show categories
        else {
            this.scrollPane.setContent(this.categoryGridPane);
        }
    }

    @FXML
    private void tablesButtonHandler() {
        //change background of buttons
        this.tablesButtonImage.setImage(TABLES_BUTTON_ACTIVE_IMAGE);
        this.ordersButtonImage.setImage(ORDERS_BUTTON_INACTIVE_IMAGE);

        this.scrollPane.setContent(this.tableGridPane);

        //add hyperlink
        this.hyperlinkHBox.getChildren().clear();
        Label tablesHyperlink = new Label("TABLES");
        tablesHyperlink.getStyleClass().add("topNavigationHyperlink");
        this.hyperlinkHBox.getChildren().add(tablesHyperlink);
    }



    @FXML
    private void incrementQuantityButtonHandler() {
        Map.Entry<Product, Integer> productIntegerEntry = this.cartTableView.getSelectionModel().getSelectedItem();
        if (productIntegerEntry == null && this.selectedProduct != null) {
            productIntegerEntry = new AbstractMap.SimpleEntry<>(this.selectedProduct, 0);
            this.cartTableView.getItems().add(0, productIntegerEntry);
            addToProductCountLabel(1);
            this.cartTableView.getSelectionModel().select(0);
        }
        if (productIntegerEntry != null) {
            Integer quantity = productIntegerEntry.getValue();
            productIntegerEntry.setValue(quantity + 1);
            this.productQuantityLabel.setText(String.valueOf(quantity + 1));
            calculateSumLabels();
            this.cartTableView.refresh();
        }
    }

    @FXML
    private void decrementQuantityButtonHandler() {
        //decrement quantity and removes it if it reaches 0
        Map.Entry<Product, Integer> productIntegerEntry = this.cartTableView.getSelectionModel().getSelectedItem();
        if (productIntegerEntry != null) {
            Integer quantity = productIntegerEntry.getValue();
            productIntegerEntry.setValue(quantity - 1);
            if (productIntegerEntry.getValue() == 0) {
                this.cartTableView.getItems().remove(productIntegerEntry);
                addToProductCountLabel(-1);
                this.productQuantityLabel.setText("0");
            } else {
                this.productQuantityLabel.setText(String.valueOf(quantity - 1));
            }
            calculateSumLabels();
            this.cartTableView.refresh();
        }
    }

    @FXML
    private void cartScrollDownButtonHandler() {
        this.cartTableView.getSelectionModel().selectBelowCell();
    }

    @FXML
    private void cartScrollUpButtonHandler() {
        this.cartTableView.getSelectionModel().selectAboveCell();
    }

    @FXML
    private void deleteButtonHandler() {
        if (this.cartTableView.getItems().isEmpty())
            emptyCartWarning();
        else
            this.cartTableView.getItems().clear();
    }

    @FXML
    private void orderButtonHandler() {
        if (this.cartTableView.getItems().isEmpty()) {
            emptyCartWarning();
        }
        else if (this.orderDto != null && this.lastToggledTableButton != null) {
            tablesButtonHandler();
            this.lastToggledTableButton.setId("tableUnavaliableToggleButton");

            Map<Product, Integer> map = new HashMap<>();
            for (Map.Entry<Product, Integer> entry : this.cartTableView.getItems()) {
                map.put(entry.getKey(), entry.getValue());
            }

            this.orderDto.setProducts(map);

            BarTable barTable = (BarTable) this.lastToggledTableButton.getUserData();
            this.orderService.createOrUpdateOrder(this.orderDto);
            this.orderDto = this.orderService.findOpenOrderByTable(barTable.getId());
        }
    }

    @FXML
    private void payButtonHandler() throws IOException {
        if (this.cartTableView.getItems().isEmpty()) {
            emptyCartWarning();
        }
        else if (this.lastToggledTableButton != null && this.orderDto != null) {
            BarTable barTable = (BarTable) this.lastToggledTableButton.getUserData();

            //checks if order has been saved to DB
            if (this.orderService.findOpenOrderByTable(barTable.getId()) != null) {
                this.mainMenuPane.setDisable(true);
                this.contentPane.getTop().setDisable(true);
                this.contentPane.getRight().setDisable(true);

                Parent payView = this.springFXMLLoader.load(ViewElementPath.PAY_VIEW.getViewPath());
                this.scrollPane.setContent(payView);
                createNumPadButtonForPayView();
                this.payViewTotalSum.setText(this.totalSumLabel.getText().substring(1));
                this.payViewTax.setText(this.totalTaxLabel.getText().substring(1));
                this.invalidInputLabel.setVisible(false);
            }
        }
    }

    @FXML
    private void payViewMakePaymentHandler() {
        if (this.payViewCash.getText().isEmpty() ||
                (Double.parseDouble(this.payViewTotalSum.getText())) > Double.parseDouble(this.payViewCash.getText())){
            this.invalidInputLabel.setVisible(true);
        }
        else if (this.orderDto != null) {
            tablesButtonHandler();
            this.lastToggledTableButton.setId("tableToggleButton");

            this.orderService.closeOrder(this.orderDto.getOrderId());

            this.orderDto = null;
            this.cartTableView.getItems().clear();
            this.productCountLabel.setText("0");
            this.selectedProduct = null;
            this.productLabel.setText("");
            this.productPriceLabel.setText("$0.00");
            this.productQuantityLabel.setText("0");
            calculateSumLabels();
            this.scrollPane.setDisable(true);


            //TODO Add Invoice

            //this runs after invoice choice is made
            this.mainMenuPane.setDisable(false);
            this.contentPane.getTop().setDisable(false);
            this.contentPane.getRight().setDisable(false);
            this.scrollPane.setDisable(false);
        }
    }

    @FXML
    private void payViewCancelHandler() {
        this.mainMenuPane.setDisable(false);
        this.contentPane.getTop().setDisable(false);
        this.contentPane.getRight().setDisable(false);
        tablesButtonHandler();
    }

    private Button createProductButton(Product product) {
        Button button = new Button();
        button.setId("productButton");

        button.setOnAction(e -> {
            //add hyperlink
            Label productHyperlink = new Label(product.getName().toUpperCase());
            productHyperlink.getStyleClass().add("topNavigationHyperlink");
            if (this.hyperlinkHBox.getChildren().size() > 3) {
                this.hyperlinkHBox.getChildren().remove(3, this.hyperlinkHBox.getChildren().size());
            }
            Label hyperlinkSeperator = new Label(">");
            hyperlinkSeperator.getStyleClass().add("topNavigationTextSeparator");
            this.hyperlinkHBox.getChildren().add(hyperlinkSeperator);
            this.hyperlinkHBox.getChildren().add(productHyperlink);

            boolean isNotDuplicate = true;
            for (Map.Entry<Product, Integer> productIntegerEntry : this.cartTableView.getItems()) {
                if (product.getName().equals(productIntegerEntry.getKey().getName())) {
                    this.cartTableView.getSelectionModel().select(productIntegerEntry);
                    isNotDuplicate = false;
                    break;
                }
            }

            if (isNotDuplicate) {
                this.productLabel.setText(product.getName());
                this.productQuantityLabel.setText("0");
                this.productPriceLabel.setText(String.format(Locale.US, "$%.2f", product.getPrice()));
                calculateSumLabels();
                this.cartTableView.getSelectionModel().clearSelection();
                this.selectedProduct = product;
            }
        });

        return button;
    }

    private Button createCategoryButton(Category category) {
        Button button = new Button(category.getName());
        button.setId("categoryButton");

        button.setOnAction(e -> {
            //add hyperlink
            Hyperlink categoryHyperlink = new Hyperlink(category.getName().toUpperCase());
            categoryHyperlink.getStyleClass().add("topNavigationHyperlink");
            categoryHyperlink.setOnAction(event -> {
                if (this.hyperlinkHBox.getChildren().size() > 3) {
                    this.hyperlinkHBox.getChildren().remove(3, this.hyperlinkHBox.getChildren().size());
                }
                createProductGrid(category);
                fillProductGrid(category);
                this.scrollPane.setContent(this.productGridPane);
            });
            Label hyperlinkSeperator = new Label(">");
            hyperlinkSeperator.getStyleClass().add("topNavigationTextSeparator");
            this.hyperlinkHBox.getChildren().add(hyperlinkSeperator);
            this.hyperlinkHBox.getChildren().add(categoryHyperlink);

            createProductGrid(category);
            fillProductGrid(category);
            this.scrollPane.setContent(this.productGridPane);
        });

        return button;
    }

    private ToggleButton createTableToggleButton(BarTable table) {
        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setText(String.valueOf(table.getNumber()));
        toggleButton.setUserData(table);

        if (table.getAvailable())
            toggleButton.setId("tableToggleButton");
        else
            toggleButton.setId("tableUnavaliableToggleButton");

        toggleButton.setOnAction(e -> this.selectedTableNumber.setText(toggleButton.getText()));

        toggleButton.setToggleGroup(this.toggleGroup);

        return toggleButton;
    }

    private void fillTablesGrid() {

        for (int i = 0; i < this.barTableList.size(); i++) {
            ToggleButton button = createTableToggleButton(this.barTableList.get(i));

            GridPane.setHalignment(button, HPos.CENTER);
            this.tableGridPane.add(button, i % TABLES_GRID_COLUMNS, (int) Math.ceil(i / TABLES_GRID_COLUMNS));
        }
    }

    private void createTablesGrid() {
        this.tableGridPane = new GridPane();
        this.tableGridPane.setStyle("-fx-background-color: transparent;");
        this.tableGridPane.setHgap(10.0);
        this.tableGridPane.setVgap(10.0);

        int rowsCount = (int) Math.ceil(this.barTableList.size() / (double) TABLES_GRID_COLUMNS);

        ColumnConstraints columnConstraints = new ColumnConstraints(150);
        RowConstraints rowConstraints = new RowConstraints(120);

        for (int i = 0; i < TABLES_GRID_COLUMNS; i++) {
            this.tableGridPane.getColumnConstraints().add(columnConstraints);
        }

        for (int j = 0; j < rowsCount; j++) {
            this.tableGridPane.getRowConstraints().add(rowConstraints);
        }
    }

    private void fillCategoryGrid() {

        for (int i = 0; i < this.categoryList.size(); i++) {
            Button button = createCategoryButton(this.categoryList.get(i));

            GridPane.setHalignment(button, HPos.CENTER);
            this.categoryGridPane.add(button, i % PRODUCT_CATEGORY_GRID_COLUMNS,
                    (int) Math.ceil(i / PRODUCT_CATEGORY_GRID_COLUMNS));
        }
    }

    private void createCategoryGrid() {
        this.categoryGridPane = new GridPane();
        this.categoryGridPane.setStyle("-fx-background-color: transparent;");
        this.categoryGridPane.setHgap(10.0);
        this.categoryGridPane.setVgap(10.0);

        int rowsCount = (int) Math.ceil(this.categoryList.size() /
                (double) PRODUCT_CATEGORY_GRID_COLUMNS);

        ColumnConstraints columnConstraints = new ColumnConstraints(110);
        RowConstraints rowConstraints = new RowConstraints(90);

        for (int i = 0; i < PRODUCT_CATEGORY_GRID_COLUMNS; i++) {
            this.categoryGridPane.getColumnConstraints().add(columnConstraints);
        }

        for (int j = 0; j < rowsCount; j++) {
            this.categoryGridPane.getRowConstraints().add(rowConstraints);
        }
    }

    private void fillProductGrid(Category category) {
        List<Product> productList = this.productService.getProductsByCategory(category);

        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            Button button = createProductButton(product);

            GridPane.setHalignment(button, HPos.CENTER);
            this.productGridPane.add(button, i % PRODUCT_CATEGORY_GRID_COLUMNS,
                    (int) Math.ceil(i / PRODUCT_CATEGORY_GRID_COLUMNS));

            Label productNameLabel = new Label(product.getName());
            productNameLabel.setPadding(new Insets(10, 0, 0, 10));
            productNameLabel.setId("productButtonLabels");
            GridPane.setHalignment(productNameLabel, HPos.LEFT);
            GridPane.setValignment(productNameLabel, VPos.TOP);
            this.productGridPane.add(productNameLabel, i % PRODUCT_CATEGORY_GRID_COLUMNS,
                    (int) Math.ceil(i / PRODUCT_CATEGORY_GRID_COLUMNS));

            Label productPriceLabel = new Label(String.format(Locale.US, "$%.2f", product.getPrice()));
            productPriceLabel.setPadding(new Insets(0, 10, 10, 0));
            productPriceLabel.setId("productButtonLabels");
            GridPane.setHalignment(productPriceLabel, HPos.RIGHT);
            GridPane.setValignment(productPriceLabel, VPos.BOTTOM);
            this.productGridPane.add(productPriceLabel, i % PRODUCT_CATEGORY_GRID_COLUMNS,
                    (int) Math.ceil(i / PRODUCT_CATEGORY_GRID_COLUMNS));
        }
    }

    private void createProductGrid(Category category) {
        List<Product> productList = this.productService.getProductsByCategory(category);

        this.productGridPane = new GridPane();
        this.productGridPane.setStyle("-fx-background-color: transparent;");
        this.productGridPane.setHgap(10.0);
        this.productGridPane.setVgap(10.0);

        int rowsCount = (int) Math.ceil(productList.size() /
                (double) PRODUCT_CATEGORY_GRID_COLUMNS);

        ColumnConstraints columnConstraints = new ColumnConstraints(110);
        RowConstraints rowConstraints = new RowConstraints(90);

        for (int i = 0; i < PRODUCT_CATEGORY_GRID_COLUMNS; i++) {
            this.productGridPane.getColumnConstraints().add(columnConstraints);
        }

        for (int j = 0; j < rowsCount; j++) {
            this.productGridPane.getRowConstraints().add(rowConstraints);
        }
    }

    private void addToProductCountLabel(int num) {
        this.productCountLabel.setText(String.valueOf(Integer.parseInt(this.productCountLabel.getText()) + num));
    }

    private void createNumPadButtonForPayView() {
        int digitCounter = 1;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button digitNumPadButton = new Button();
                digitNumPadButton.setText(String.valueOf(digitCounter));
                digitNumPadButton.getStyleClass().add("payViewNumPadButtons");
                digitNumPadButton.setOnAction(e -> {
                    if (checkValidNumberInput()) {
                        this.payViewCash.setText(this.payViewCash.getText() + digitNumPadButton.getText());
                        this.payViewChange.setText(String.format("%.2f",
                                Double.parseDouble(this.payViewCash.getText()) -
                                        Double.parseDouble(this.payViewTotalSum.getText())));
                    }
                });
                this.payViewGridPane.add(digitNumPadButton, j, i);
                digitCounter++;
            }
        }

        Button dotNumPadButton = new Button();
        dotNumPadButton.getStyleClass().add("payViewNumPadButtons");
        dotNumPadButton.setText(".");
        dotNumPadButton.setOnAction(e -> {
            if (checkValidDotInput()) {
                this.payViewCash.setText(this.payViewCash.getText() + dotNumPadButton.getText());
            }
        });
        this.payViewGridPane.add(dotNumPadButton, 0, 3);

        Button zeroNumPadButton = new Button();
        zeroNumPadButton.getStyleClass().add("payViewNumPadButtons");
        zeroNumPadButton.setText("0");
        zeroNumPadButton.setOnAction(e -> {
            if (checkValidNumberInput()) {
                this.payViewCash.setText(this.payViewCash.getText() + zeroNumPadButton.getText());
                this.payViewChange.setText(String.format("%.2f", Double.parseDouble(this.payViewCash.getText()) -
                        Double.parseDouble(this.payViewTotalSum.getText())));
            }
        });
        this.payViewGridPane.add(zeroNumPadButton, 1, 3);

        Button deleteNumPadButton = new Button();
        deleteNumPadButton.getStyleClass().add("payViewNumPadButtons");
        deleteNumPadButton.setText("X");
        deleteNumPadButton.setOnAction(e -> {
            if (!this.payViewCash.getText().isEmpty()) {
                this.invalidInputLabel.setVisible(false);
                this.payViewCash.setText(this.payViewCash.getText()
                        .substring(0, this.payViewCash.getText().length() - 1));
            }
        });
        this.payViewGridPane.add(deleteNumPadButton, 2, 3);
    }

    private boolean checkValidNumberInput() {
        String amount = this.payViewCash.getText();
        if (amount.isEmpty()) {
            this.invalidInputLabel.setVisible(false);
            return true;
        }
        if (amount.charAt(0) == '0' && amount.length() == 1) {
            this.invalidInputLabel.setVisible(true);
            return false;
        }
        this.invalidInputLabel.setVisible(false);
        return true;
    }

    private boolean checkValidDotInput() {
        String amount = this.payViewCash.getText();
        if (amount.isEmpty()) {
            this.invalidInputLabel.setVisible(true);
            return false;
        }
        if (amount.contains(".")) {
            this.invalidInputLabel.setVisible(true);
            return false;
        }
        this.invalidInputLabel.setVisible(false);
        return true;
    }

    private void calculateSumLabels() {
        double sum = Double.parseDouble(this.productPriceLabel.getText().substring(1)) *
                Integer.parseInt(this.productQuantityLabel.getText());

        this.productTotalSumLabel.setText(String.format(Locale.US, "$%.2f", sum));
        sum = 0.0;
        for (Map.Entry<Product, Integer> entry : this.cartTableView.getItems()) {
            sum += Double.parseDouble(this.totalSumColumn.getCellObservableValue(entry).getValue().substring(1));
        }
        this.totalSumLabel.setText(String.format(Locale.US, "$%.2f", sum));
        this.totalTaxLabel.setText(String.format(Locale.US, "$%.2f", sum * 0.2));
    }

    private void initiateCart() {
        //set the columns of cart
        this.productColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getKey().getName()));
        this.quantityColumn.setCellValueFactory(param ->
                new SimpleStringProperty(String.valueOf(param.getValue().getValue())));
        this.priceColumn.setCellValueFactory(param ->
                new SimpleStringProperty(String.format(Locale.US, "$%.2f", param.getValue().getKey().getPrice())));
        this.totalSumColumn.setCellValueFactory(param ->
                new SimpleStringProperty(String.format(Locale.US, "$%.2f", param.getValue().getKey().getPrice())));

        this.priceColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        this.totalSumColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        this.quantityColumn.setStyle("-fx-alignment: CENTER-RIGHT;");


        //selected item in cart shows quantity in label
        this.cartTableView.getSelectionModel().selectedIndexProperty().addListener(e -> {
            Map.Entry<Product, Integer> selectedItem = this.cartTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                this.productLabel.setText(selectedItem.getKey().getName());
                this.productQuantityLabel.setText(String.valueOf(selectedItem.getValue()));
                this.productPriceLabel.setText(this.priceColumn.getCellObservableValue(selectedItem).getValue());
                calculateSumLabels();
            }
        });
    }

    private void timeInfo() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime localDateTime = LocalDateTime.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd MMM HH:mm", Locale.ENGLISH);
            this.currentTimeLabel.setText(localDateTime.format(timeFormatter));
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void emptyCartWarning() {
        if (this.cartEmptyWarningThread == null || !this.cartEmptyWarningThread.isAlive()) {
            this.cartEmptyWarningThread = new Thread(() -> {
                emptyCartLabel.setId("emptyCartLabelWarning");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                emptyCartLabel.setId("emptyCartLabel");
            });
            this.cartEmptyWarningThread.start();
        }
    }

    //TODO Remove before building
    private void initUserDev() {
        this.currentUser = new User();
        this.currentUser.setName("Pesho");
        this.currentUser.setRole("dasda");
    }

    private ObservableList<Map.Entry<Product, Integer>> initOrder() {
        Product product = new Product();
        product.setName("Bira");
        product.setPrice(10.0);
        ObservableList<Map.Entry<Product, Integer>> list = FXCollections.observableArrayList();
        Map<Product, Integer> map = new HashMap<>();
        map.put(product, 5);
        list.addAll(map.entrySet());

        addToProductCountLabel(1);
        return list;
    }
}
