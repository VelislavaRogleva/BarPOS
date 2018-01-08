package app.controllers;

import app.cores.StageManager;
import app.dtos.OrderDto;
import app.entities.*;
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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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
            productPriceLabel, productTotalSumLabel, productCountLabel, totalSumLabel, totalTaxLabel;
    @FXML
    private GridPane tableGridPane, productGridPane, categoryGridPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private HBox hyperlinkHBox;
    @FXML
    private TableView<Map.Entry<Product, Integer>> cartTableView;
    @FXML
    private TableColumn<Map.Entry<Product, Integer>, String> productColumn;
    @FXML
    private TableColumn<Map.Entry<Product, Integer>, String> quantityColumn;
    @FXML
    private TableColumn<Map.Entry<Product, Integer>, String> priceColumn, totalSumColumn;

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

    @Autowired
    @Lazy
    public SaleController(StageManager stageManager, BarTableService barTableService,
                          OrderService orderService, CategoryService categoryService, ProductService productService) {
        this.stageManager = stageManager;
        this.barTableList = barTableService.getAllBarTables();
        this.orderService = orderService;
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @Override
    public void initialize() {
        this.categoryList = this.categoryService.getAllCategories();
        this.currentUser = this.stageManager.getUser();

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
            this.productLabel.setText(selectedItem.getKey().getName());
            this.productQuantityLabel.setText(String.valueOf(selectedItem.getValue()));
            this.productPriceLabel.setText(this.priceColumn.getCellObservableValue(selectedItem).getValue());
            calculateProductTotalSumLabel();
        });

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
           }
           else {
               //Fill cart with products when tables is selected
               this.lastToggledTableButton = (ToggleButton) newValue;
               BarTable barTable = (BarTable) this.lastToggledTableButton.getUserData();
               this.orderDto = this.orderService.findOpenOrderByTable(barTable.getId());

               if (this.orderDto == null) {
                   this.orderDto = new OrderDto();
                   this.orderDto.setUser(this.currentUser);
                   this.orderDto.setBarTable(barTable);
               }
               else {
                   ObservableList<Map.Entry<Product, Integer>> observableList = FXCollections.observableArrayList();
                   observableList.addAll(this.orderDto.getProducts().entrySet());
                   this.cartTableView.setItems(observableList);
                   addToProductCountLabel(this.cartTableView.getItems().size());
               }
           }
        });

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

    private void timeInfo(){
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

    @FXML
    private void logout(){
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
        ordersHyperlink.setOnAction(e -> {
            ordersButtonHandler();
        });
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
                calculateProductTotalSumLabel();
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

        toggleButton.setOnAction(e -> {
            this.selectedTableNumber.setText(toggleButton.getText());
        });

        toggleButton.setToggleGroup(this.toggleGroup);

        return toggleButton;
    }

    private void fillTablesGrid() {

        for (int i = 0; i < this.barTableList.size(); i++) {
            ToggleButton button = createTableToggleButton(this.barTableList.get(i));

            GridPane.setHalignment(button, HPos.CENTER);
            this.tableGridPane.add(button,i % TABLES_GRID_COLUMNS, (int) Math.ceil(i / TABLES_GRID_COLUMNS));
        }
    }

    private void createTablesGrid() {
        this.tableGridPane = new GridPane();
        this.tableGridPane.setStyle("-fx-background-color: transparent;");
        this.tableGridPane.setHgap(10.0);
        this.tableGridPane.setVgap(10.0);

        int rowsCount = (int) Math.ceil(this.barTableList.size() / (double)TABLES_GRID_COLUMNS);

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
            this.categoryGridPane.add(button,i % PRODUCT_CATEGORY_GRID_COLUMNS,
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
            this.productGridPane.add(button,i % PRODUCT_CATEGORY_GRID_COLUMNS,
                    (int) Math.ceil(i / PRODUCT_CATEGORY_GRID_COLUMNS));

            Label productNameLabel = new Label(product.getName());
            productNameLabel.setPadding(new Insets(10, 0, 0, 10));
            productNameLabel.setId("productButtonLabels");
            GridPane.setHalignment(productNameLabel, HPos.LEFT);
            GridPane.setValignment(productNameLabel, VPos.TOP);
            this.productGridPane.add(productNameLabel,i % PRODUCT_CATEGORY_GRID_COLUMNS,
                    (int) Math.ceil(i / PRODUCT_CATEGORY_GRID_COLUMNS));

            Label productPriceLabel = new Label(String.format(Locale.US, "$%.2f", product.getPrice()));
            productPriceLabel.setPadding(new Insets(0, 10, 10, 0));
            productPriceLabel.setId("productButtonLabels");
            GridPane.setHalignment(productPriceLabel, HPos.RIGHT);
            GridPane.setValignment(productPriceLabel, VPos.BOTTOM);
            this.productGridPane.add(productPriceLabel,i % PRODUCT_CATEGORY_GRID_COLUMNS,
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

    private void calculateProductTotalSumLabel() {
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

    @FXML
    private void incrementQuantityButtonHandler() {
        Map.Entry<Product, Integer> productIntegerEntry = this.cartTableView.getSelectionModel().getSelectedItem();
        if (productIntegerEntry == null) {
            productIntegerEntry = new AbstractMap.SimpleEntry<>(this.selectedProduct, 0);
            this.cartTableView.getItems().add(0, productIntegerEntry);
            addToProductCountLabel(1);
            this.cartTableView.getSelectionModel().select(0);
        }
        Integer quantity = productIntegerEntry.getValue();
        productIntegerEntry.setValue(quantity + 1);
        this.productQuantityLabel.setText(String.valueOf(quantity + 1));
        calculateProductTotalSumLabel();
        this.cartTableView.refresh();
    }

    @FXML
    private void decrementQuantityButtonHandler() {
        //decrement quantity and removes it if it reaches 0
        Map.Entry<Product, Integer> productIntegerEntry = this.cartTableView.getSelectionModel().getSelectedItem();
        Integer quantity = productIntegerEntry.getValue();
        productIntegerEntry.setValue(quantity - 1);
        if (productIntegerEntry.getValue() == 0) {
            this.cartTableView.getItems().remove(productIntegerEntry);
            addToProductCountLabel(-1);
            this.productQuantityLabel.setText("0");
        }
        else {
            this.productQuantityLabel.setText(String.valueOf(quantity - 1));
        }
        calculateProductTotalSumLabel();
        this.cartTableView.refresh();
    }

    private void addToProductCountLabel(int num) {
        this.productCountLabel.setText(String.valueOf(Integer.parseInt(this.productCountLabel.getText()) + num));
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
        this.cartTableView.getItems().clear();
    }

    @FXML
    private void orderButtonHandler() {
        Map<Product, Integer> map = new HashMap<>();
        for (Map.Entry<Product, Integer> entry : this.cartTableView.getItems()) {
            map.put(entry.getKey(), entry.getValue());
        }

        this.orderDto.setProducts(map);

        this.orderService.createOrUpdateOrder(this.orderDto);
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
        for (Map.Entry<Product, Integer> entry : map.entrySet()) {
            list.add(entry);
        }

        addToProductCountLabel(1);
        return list;
    }
}
