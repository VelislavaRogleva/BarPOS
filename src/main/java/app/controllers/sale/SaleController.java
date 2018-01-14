package app.controllers.sale;

import app.controllers.FxmlController;
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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class SaleController implements FxmlController {

    private final int TABLES_GRID_COLUMNS = 3;
    private final int PRODUCT_CATEGORY_GRID_COLUMNS = 4;

    @FXML
    public Button logoutButton, managerButton, incrementQuantityButton, decrementQuantityButton;
    @FXML
    public ImageView tablesButtonImage, ordersButtonImage, managerButtonImage;
    @FXML
    private Label currentTimeLabel, currentUserLabel, selectedTableNumber, productLabel, productQuantityLabel,
            productPriceLabel, productTotalSumLabel, productCountLabel, totalSumLabel, totalTaxLabel, alertLabel;
    @FXML
    private GridPane tableGridPane, productGridPane, categoryGridPane;
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
    private OrderService orderService;
    private ToggleGroup toggleGroup;
    private OrderDto orderDto;
    private ToggleButton lastToggledTableButton;
    private CategoryService categoryService;
    private ProductService productService;
    private Product selectedProduct;
    private Thread alertThread;
    private BarTableService barTableService;

    @Autowired
    @Lazy
    public SaleController(StageManager stageManager, BarTableService barTableService,
                          OrderService orderService, CategoryService categoryService,
                          ProductService productService) {
        this.stageManager = stageManager;
        this.orderService = orderService;
        this.categoryService = categoryService;
        this.productService = productService;
        this.barTableService = barTableService;
    }

    @Override
    public void initialize() {

        this.currentUser = this.stageManager.getUser();

        if (this.currentUser != null) {
            //gets UserName next to clock
            this.currentUserLabel.setText(this.currentUser.getName());

            //Hide managerButton if user is not admin
            if (!"admin".equalsIgnoreCase(this.currentUser.getRole())) {
                this.managerButton.setDisable(true);
                this.managerButtonImage.setOpacity(0);
            }
        }

        //ToggleButtons act like RadioButtons
        this.toggleGroup = new ToggleGroup();
        this.toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.lastToggledTableButton = null;
                this.orderDto = null;
                this.selectedTableNumber.setText("-");
                this.cartTableView.getItems().clear();
                this.productCountLabel.setText("0");
                nullifySelectedProduct();
                calculateSumLabels();
            } else {
                //Fill cart with products when tables is selected
                this.lastToggledTableButton = (ToggleButton) newValue;
                this.selectedTableNumber.setText(this.lastToggledTableButton.getText());
                BarTable barTable = (BarTable) this.lastToggledTableButton.getUserData();
                this.orderDto = this.orderService.findOpenOrderByTable(barTable.getId());

                nullifySelectedProduct();

                //Loads Order or clears cart
                if (this.orderDto == null) {
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
        this.tablesButtonImage.setImage(new Image("static_data/images/tableButton.png"));
        this.ordersButtonImage.setImage(new Image("static_data/images/ordersButtonActive.png"));

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
        this.tablesButtonImage.setImage(new Image("static_data/images/tableButtonActive.png"));
        this.ordersButtonImage.setImage(new Image("static_data/images/ordersButton.png"));

        this.scrollPane.setContent(this.tableGridPane);

        for (Toggle button : this.toggleGroup.getToggles()) {
            long tableId = ((BarTable) button.getUserData()).getId();
            ToggleButton toggleButton = (ToggleButton) button;
            if (this.orderService.findOpenOrderByTable(tableId) == null) {
                toggleButton.getStyleClass().clear();
                toggleButton.getStyleClass().add("tableToggleButton");
            } else {
                toggleButton.getStyleClass().clear();
                toggleButton.getStyleClass().add("tableUnavaliableToggleButton");
            }
        }

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
            addToProductCountLabel();
            this.cartTableView.getSelectionModel().select(0);
        }
        if (productIntegerEntry != null) {
            Integer quantity = productIntegerEntry.getValue();

            if (productIntegerEntry.getKey().getStockQuantity() <= quantity) {
                alertLabelWarning("Not enough in stock");
            } else {
                productIntegerEntry.setValue(quantity + 1);
                this.productQuantityLabel.setText(String.valueOf(quantity + 1));
                calculateSumLabels();
                this.cartTableView.refresh();
            }
        }
    }

    @FXML
    private void decrementQuantityButtonHandler() {
        //decrement quantity and removes it if it reaches 0
        Map.Entry<Product, Integer> productIntegerEntry = this.cartTableView.getSelectionModel().getSelectedItem();
        if (productIntegerEntry != null) {
            Integer quantity = productIntegerEntry.getValue();
            if (productIntegerEntry.getValue() > 0) {
                productIntegerEntry.setValue(quantity - 1);
                this.productQuantityLabel.setText(String.valueOf(quantity - 1));
            }
            calculateSumLabels();
            this.cartTableView.refresh();
        }
    }

    @FXML
    private void cartScrollDownButtonHandler() {
        if (this.cartTableView.getSelectionModel().isEmpty()) {
            this.cartTableView.getSelectionModel().select(0);
        }
        else {
            this.cartTableView.getSelectionModel().selectBelowCell();
        }
    }

    @FXML
    private void cartScrollUpButtonHandler() {
        if (this.cartTableView.getSelectionModel().isEmpty()) {
            this.cartTableView.getSelectionModel().select(0);
        }
        else {
            this.cartTableView.getSelectionModel().selectAboveCell();
        }
    }

    @FXML
    private void orderButtonHandler() {
        if (this.cartTableView.getItems().isEmpty()) {
            alertLabelWarning("The cart is empty");
        } else if (this.lastToggledTableButton != null) {

            if (this.orderDto == null) {
                this.orderDto = new OrderDto();
                this.orderDto.setUser(this.currentUser);
                this.orderDto.setBarTable((BarTable) this.lastToggledTableButton.getUserData());
            }

            if (this.cartTableView.getItems().stream().noneMatch(kvp -> kvp.getValue() > 0)) {
                cancelOrderButtonHandler();
            }
            else {
                Map<Product, Integer> map = new HashMap<>();
                for (Map.Entry<Product, Integer> entry : this.cartTableView.getItems()) {
                    map.put(entry.getKey(), entry.getValue());
                }

                this.orderDto.setProducts(map);

                BarTable barTable = (BarTable) this.lastToggledTableButton.getUserData();
                this.orderService.createOrUpdateOrder(this.orderDto);
                this.orderDto = this.orderService.findOpenOrderByTable(barTable.getId());

                ObservableList<Map.Entry<Product, Integer>> observableList = FXCollections.observableArrayList();
                observableList.addAll(this.orderDto.getProducts().entrySet());
                this.cartTableView.setItems(observableList);
                this.productCountLabel.setText(String.valueOf(this.cartTableView.getItems().size()));

                tablesButtonHandler();
                alertLabelWarning("Order sent");
            }
        }
    }

    @FXML
    private void cancelOrderButtonHandler() {
        if (this.orderDto == null) {
            alertLabelWarning("There is no Order");
        } else {
            Pane pane = new Pane();
            Label questionLabel = new Label("Are you sure you want to cancel order?");
            Button yesButton = new Button("OK");
            Button noButton = new Button("Cancel");
            Stage stage = new Stage();

            yesButton.setOnAction(e -> {
                cancelOrder();
                stage.close();
            });

            noButton.setOnAction(e -> stage.close());

            pane.getChildren().addAll(questionLabel, yesButton, noButton);
            pane.getStyleClass().add("editDialogBox");
            pane.getStylesheets().add("static_data/manager.css");
            questionLabel.setLayoutY(50);
            questionLabel.setLayoutX(30);
            yesButton.setLayoutX(43);
            yesButton.setLayoutY(130);
            yesButton.getStyleClass().add("okDialogButton");
            noButton.setLayoutX(182);
            noButton.setLayoutY(130);
            noButton.getStyleClass().add("cancelDialogButton");

            questionLabel.setFont(Font.font(18));

            Scene scene = new Scene(pane, 401, 188);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();
        }
    }

    @FXML
    private void payButtonHandler() {
        if (this.orderDto == null) {
            alertLabelWarning("There is no Order");
        } else if (this.lastToggledTableButton != null) {
            BarTable barTable = (BarTable) this.lastToggledTableButton.getUserData();

            //checks if order has been saved to DB
            if (this.orderService.findOpenOrderByTable(barTable.getId()) != null) {
                this.mainMenuPane.setDisable(true);
                this.contentPane.getTop().setDisable(true);
                this.contentPane.getRight().setDisable(true);

                Parent payView = this.stageManager.getPane(ViewElementPath.PAY_VIEW);
                this.scrollPane.setContent(payView);
                PayViewController payViewController = this.stageManager.getController();

                payViewController.setPayViewTotalSum(this.totalSumLabel.getText().substring(1));
                payViewController.setPayViewTax(String.format("$%s", this.totalTaxLabel.getText().substring(1)));
                payViewController.setOrderDto(this.orderDto);
                payViewController.setSaleController(this);
            }
        }
    }

    @FXML
    public void payViewCancelPayment() {
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
            addHyperlinkToAction(product);
        });

        return button;
    }

    private void addHyperlinkToAction(Product product) {
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

        if (table.getAvailable()) {
            toggleButton.getStyleClass().clear();
            toggleButton.getStyleClass().add("tableToggleButton");
        } else {
            toggleButton.getStyleClass().clear();
            toggleButton.getStyleClass().add("tableUnavaliableToggleButton");
        }

        toggleButton.setToggleGroup(this.toggleGroup);

        return toggleButton;
    }

    private void fillTablesGrid() {
        List<BarTable> barTableList = this.barTableService.getAllBarTables();

        for (int i = 0; i < barTableList.size(); i++) {
            ToggleButton button = createTableToggleButton(barTableList.get(i));

            GridPane.setHalignment(button, HPos.CENTER);
            this.tableGridPane.add(button, i % TABLES_GRID_COLUMNS, (int) Math.ceil(i / TABLES_GRID_COLUMNS));
        }
    }

    private void createTablesGrid() {
        List<BarTable> barTableList = this.barTableService.getAllBarTables();
        this.tableGridPane = new GridPane();
        this.tableGridPane.setStyle("-fx-background-color: transparent;");
        this.tableGridPane.setHgap(10.0);
        this.tableGridPane.setVgap(10.0);

        int rowsCount = (int) Math.ceil(barTableList.size() / (double) TABLES_GRID_COLUMNS);

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
        List<Category> categoryList = this.categoryService.getAllCategories();

        for (int i = 0; i < categoryList.size(); i++) {
            Button button = createCategoryButton(categoryList.get(i));
            String multiLineName = button.getText().replace(" ", "\n").replace("-", "\n");
            button.setText(multiLineName);

            GridPane.setHalignment(button, HPos.CENTER);
            this.categoryGridPane.add(button, i % PRODUCT_CATEGORY_GRID_COLUMNS,
                    (int) Math.ceil(i / PRODUCT_CATEGORY_GRID_COLUMNS));
        }
    }

    private void createCategoryGrid() {
        List<Category> categoryList = this.categoryService.getAllCategories();
        this.categoryGridPane = new GridPane();
        this.categoryGridPane.setStyle("-fx-background-color: transparent;");
        this.categoryGridPane.setHgap(10.0);
        this.categoryGridPane.setVgap(10.0);

        int rowsCount = (int) Math.ceil(categoryList.size() /
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
        List<Product> productList = this.productService.getAllAvailableProductsInCategory(category);

        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            Button button = createProductButton(product);

            GridPane.setHalignment(button, HPos.CENTER);
            this.productGridPane.add(button, i % PRODUCT_CATEGORY_GRID_COLUMNS,
                    (int) Math.ceil(i / PRODUCT_CATEGORY_GRID_COLUMNS));

            Label productNameLabel = new Label(product.getName());
            productNameLabel.setPadding(new Insets(10, 0, 0, 10));
            productNameLabel.setId("productButtonLabels");
            productNameLabel.setMouseTransparent(true);
            GridPane.setHalignment(productNameLabel, HPos.LEFT);
            GridPane.setValignment(productNameLabel, VPos.TOP);
            this.productGridPane.add(productNameLabel, i % PRODUCT_CATEGORY_GRID_COLUMNS,
                    (int) Math.ceil(i / PRODUCT_CATEGORY_GRID_COLUMNS));

            Label productPriceLabel = new Label(String.format(Locale.US, "$%.2f", product.getPrice()));
            productPriceLabel.setPadding(new Insets(0, 10, 10, 0));
            productPriceLabel.setId("productButtonLabels");
            productPriceLabel.setMouseTransparent(true);
            GridPane.setHalignment(productPriceLabel, HPos.RIGHT);
            GridPane.setValignment(productPriceLabel, VPos.BOTTOM);
            this.productGridPane.add(productPriceLabel, i % PRODUCT_CATEGORY_GRID_COLUMNS,
                    (int) Math.ceil(i / PRODUCT_CATEGORY_GRID_COLUMNS));

            Hyperlink productDescriptionHyperlink = new Hyperlink("view");
            productDescriptionHyperlink.setPadding(new Insets(0, 0, 10, 10));
            productDescriptionHyperlink.setId("productButtonViewHyperlink");
            productDescriptionHyperlink.setOnAction(e -> {
                Parent descriptionView = this.stageManager.getPane(ViewElementPath.DESCRIPTION_VIEW);
                this.scrollPane.setContent(descriptionView);
                DescriptionViewController controller = this.stageManager.getController();
                controller.setProduct(product);
                addHyperlinkToAction(product);
            });

            GridPane.setHalignment(productDescriptionHyperlink, HPos.LEFT);
            GridPane.setValignment(productDescriptionHyperlink, VPos.BOTTOM);
            this.productGridPane.add(productDescriptionHyperlink, i % PRODUCT_CATEGORY_GRID_COLUMNS,
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

    private void addToProductCountLabel() {
        this.productCountLabel.setText(String.valueOf(Integer.parseInt(this.productCountLabel.getText()) + 1));
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
                new SimpleStringProperty(String.format(Locale.US, "$%.2f", param.getValue().getKey().getPrice() *
                        param.getValue().getValue())));

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

    private void alertLabelWarning(String text) {
        this.alertLabel.setText(text);
        if (this.alertThread == null || !this.alertThread.isAlive()) {
            this.alertThread = new Thread(() -> {
                alertLabel.setOpacity(1);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                alertLabel.setOpacity(0);
            });

            this.alertThread.start();
        }
    }

    private void nullifySelectedProduct() {
        this.selectedProduct = null;
        this.productLabel.setText("");
        this.productQuantityLabel.setText("0");
        this.productPriceLabel.setText("$0.00");
    }

    private void cancelOrder() {
        this.orderService.cancelOrder(this.orderDto.getOrderId());
        this.lastToggledTableButton.getStyleClass().clear();
        this.lastToggledTableButton.getStyleClass().add("tableToggleButton");
        this.cartTableView.getItems().clear();
        this.lastToggledTableButton.setSelected(false);
        this.selectedTableNumber.setText("-");
        this.productCountLabel.setText("0");
        alertLabelWarning("Order cancelled");
    }

    public void payViewMakePayment() {

        this.orderService.closeOrder(this.orderDto.getOrderId());

        this.orderDto = null;
        this.cartTableView.getItems().clear();
        this.productCountLabel.setText("0");
        this.selectedProduct = null;
        this.productLabel.setText("");
        this.productPriceLabel.setText("$0.00");
        this.productQuantityLabel.setText("0");
        calculateSumLabels();

        this.mainMenuPane.setDisable(false);
        this.contentPane.getTop().setDisable(false);
        this.contentPane.getRight().setDisable(false);

        tablesButtonHandler();
        this.lastToggledTableButton.setSelected(false);
        this.selectedTableNumber.setText("-");
        alertLabelWarning("Order paid");
    }
}