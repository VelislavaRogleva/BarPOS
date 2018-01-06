package app.controllers.manager.viewElements;

import app.controllers.FxmlController;
import app.controllers.manager.manager_dialogs.AddButton;
import app.controllers.manager.manager_dialogs.DeleteButtonCell;
import app.controllers.manager.manager_dialogs.EditButtonCell;
import app.cores.StageManager;
import app.entities.Category;
import app.entities.Product;
import app.services.api.CategoryService;
import app.services.api.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.converter.DoubleStringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URL;

@Component
public class ManageProductController implements FxmlController {

    private static final Double BUTTON_DEFAULT_WIDTH = 90.0;
    private static final Double TABLE_DEFAULT_WIDTH = 790.0;
    private static final Double DELETE_BUTTON_OFFSET = 30.0;
    private static final int OBJECT_COUNT_PROPRTIES = 7;

    @FXML private Pane anchorPaneAddButton;
    @FXML private Pane mainContentAnchor;
    @FXML private URL location;

    private TableView genericTable;

    private ProductService productService;
    private CategoryService categoryService;
    private StageManager stageManager;

    @Autowired
    @Lazy
    public ManageProductController(ProductService productService, CategoryService categoryService, StageManager stageManager) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.stageManager = stageManager;
    }

    @Override
    public void initialize() {
        createTable();
        addButtonAction();
    }


    ///////////////////////// dev creating fake database entries ////////////////////////////////
    protected <S> ObservableList<S> getAllFakeCategories(){
        ObservableList<S> categories = FXCollections.observableArrayList();

        String[] fakeCategories = {"coffee", "beer", "cocktails", "wine", "whiskey", "soft-drink", "brandy", "water", "tea", "nuts", "bacon", "glo", "blo", "mlo"};
        Long id =1L;
        double price = 12.0;
        String barcode = "1232132132323";
        boolean available = true;

        for (String category:fakeCategories) {
            Product newCat = new Product();
            newCat.setId(id);
            newCat.setName(category);
            newCat.setPrice(price);
            newCat.setImagePath("/img/ts.png");
            newCat.setBarcode(barcode);
            newCat.setDescription("This is blq blq blq blq blq blq blq blq");
            newCat.setAvailable(available);
            Category cat =  new Category();
            cat.setId(17L);
            cat.setName("bokra");
            newCat.setCategory(cat);
            categories.add((S) newCat);
            id++;
            price += 105.0;
            available = !(price % 2 == 0);
        }
        return categories;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////



    private void createTable() {

        genericTable = new TableView();
        genericTable.getStyleClass().addAll("contentTable");
        genericTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        double columnWidth = (TABLE_DEFAULT_WIDTH - (2*BUTTON_DEFAULT_WIDTH) - DELETE_BUTTON_OFFSET)/ OBJECT_COUNT_PROPRTIES;

        // create table columns
        TableColumn<Product, Boolean> editButtonColumn = new TableColumn<>();
        setButtonColumnProperties(editButtonColumn, "editColumn");
        editButtonColumn.setCellFactory(new Callback<TableColumn<Product, Boolean>, TableCell<Product, Boolean>>() {
            @Override
            public TableCell<Product, Boolean> call(TableColumn<Product, Boolean> param) {
                EditButtonCell editButton = new EditButtonCell(stageManager);
                editButton.createButton(genericTable);
                return editButton;
            }
        });


        TableColumn<Product, String> nameColumn = new TableColumn<>("name");
        setColumnProperties(nameColumn, columnWidth);
        nameColumn.setCellFactory(TextFieldTableCell.<Product>forTableColumn());
        nameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("price");
        setColumnProperties(priceColumn, columnWidth);
        priceColumn.setCellFactory(TextFieldTableCell.<Product, Double>forTableColumn(new DoubleStringConverter()));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));

        TableColumn<Product, String> imagePathColumn = new TableColumn<>("image");
        setColumnProperties(imagePathColumn, columnWidth);
        imagePathColumn.setCellFactory(TextFieldTableCell.<Product>forTableColumn());
        imagePathColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("imagePath"));

        TableColumn<Product, String> barcodeColumn = new TableColumn<>("barcode");
        setColumnProperties(barcodeColumn, columnWidth);
        barcodeColumn.setCellFactory(TextFieldTableCell.<Product>forTableColumn());
        barcodeColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("barcode"));

        TableColumn<Product, String> descriptionColumn = new TableColumn<>("description");
        setColumnProperties(descriptionColumn, columnWidth);
        descriptionColumn.setCellFactory(TextFieldTableCell.<Product>forTableColumn());
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("description"));

        TableColumn<Product, Boolean> availableColumn = new TableColumn<>("available");
        setColumnProperties(availableColumn, columnWidth);
        //change true to YES and false to NO
        availableColumn.setCellFactory(ac-> new TableCell<Product, Boolean>(){
            @Override
            protected  void updateItem(Boolean item, boolean empty){
                super.updateItem(item, empty);
                if (empty){
                    setText("");
                } else {
                    if(item){
                        setText("YES");
                        setTextFill(Color.color(0.54, 0.67, 0.09));
                    } else{
                        setText("NO");
                        setTextFill(Color.color(0.69, 0.047, 0.18) );
                    }

                }
            }
        });
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        TableColumn<Product, Category> categoryColumn = new TableColumn<>("category");
        setColumnProperties(categoryColumn, columnWidth);
        categoryColumn.setCellFactory(cc -> new TableCell<Product, Category>(){
            @Override
            protected  void updateItem(Category item, boolean empty){
                super.updateItem(item, empty);
                if (empty){
                    setText("");
                } else {
                    setText(item.getName());
                }
            }
        });
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Product, Boolean> deleteButtonColumn = new TableColumn<>();
        setButtonColumnProperties(deleteButtonColumn, "deleteColumn");
        deleteButtonColumn.setCellFactory(new Callback<TableColumn<Product, Boolean>, TableCell<Product, Boolean>>() {
            @Override
            public TableCell<Product, Boolean> call(TableColumn<Product, Boolean> param) {
                DeleteButtonCell deleteButton = new DeleteButtonCell(stageManager);
                deleteButton.createButton(genericTable);
                return deleteButton;
            }
        });


        genericTable.getColumns().addAll(editButtonColumn, nameColumn, priceColumn, imagePathColumn, barcodeColumn, descriptionColumn, availableColumn, categoryColumn, deleteButtonColumn);


        //create content columns
        ObservableList<Product> availableProducts = getAllFakeCategories();
        if (availableProducts.size()>0) {

            genericTable.setItems(getAllFakeCategories());
            this.mainContentAnchor.getChildren().addAll(genericTable);
        }
    }

    private void setColumnProperties(TableColumn<?, ?> currentColumn, Double width) {
        currentColumn.getStyleClass().addAll("contentColumn");
        currentColumn.setPrefWidth(width);
        currentColumn.setMinWidth(width);
        currentColumn.setMaxWidth(width);
    }

    private void setButtonColumnProperties(TableColumn<?, Boolean> buttonColumn, String buttonStyleClass) {
        buttonColumn.setPrefWidth(BUTTON_DEFAULT_WIDTH);
        buttonColumn.setMaxWidth(BUTTON_DEFAULT_WIDTH);
        buttonColumn.setMinWidth(BUTTON_DEFAULT_WIDTH);
        buttonColumn.setSortable(false);
        buttonColumn.getStyleClass().addAll(buttonStyleClass);
    }

    @FXML
    private void addButtonAction(){

        String name = this.getClass().getSimpleName().replace("Manage", "").replace("Controller", "").toUpperCase();
        String entityName = String.format("%s%s",name.substring(0,1),name.substring(1).toLowerCase());
        AddButton<Product> newAddButton = new AddButton<>(this.stageManager);
        Button addButton = newAddButton.createButton(entityName, genericTable);
        this.anchorPaneAddButton.getChildren().add(addButton);
    }

}
