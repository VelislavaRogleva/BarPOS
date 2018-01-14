package app.controllers.manager;

import app.controllers.manager.crud.buttons.DeleteButtonCell;
import app.controllers.manager.crud.buttons.EditButtonCell;
import app.cores.StageManager;
import app.entities.Category;
import app.entities.Product;
import app.services.api.CategoryService;
import app.services.api.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.converter.IntegerStringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ManageProductController extends BaseManageController {


    private static final String NAME_COLUMN_TITLE = "name";
    private static final String NAME_COLUMN_VALUE_FIELD_NAME = "name";
    private static final String AVAILABLE_COLUMN_TITLE = "available";
    private static final String AVAILABLE_COLUMN_VALUE_FIELD_NAME = "available";
    private static final int OBJECT_COUNT_PROPERTIES = 9;
    private static final String GENERIC_TABLE_STYLE_CLASS_NAME = "contentTable";
    private static final String EDIT_COLUMN_TITLE = "editColumn";
    private static final String PRICE_COLUMN_TITLE = "price";
    private static final String PRICE_COLUMN_VALUE_FIELD_NAME = "price";
    private static final String COST_NAME_COLUMN = "cost";
    private static final String COST_COLUMN_VALUE_FIELD_NAME = "cost";
    private static final String IN_STOCK_COLUMN_TITLE = "in stock";
    private static final String IN_STOCK_COLUMN_VALUE_FIELD_NAME = "stockQuantity";
    private static final String IMAGE_COLUMN_TITLE = "image";
    private static final String IMAGE_COLUMN_VALUE_FIELD_NAME = "imagePath";
    private static final String BARCODE_COLUMN_TITLE = "barcode";
    private static final String BARCODE_COLUMN_VALUE_FIELD_NAME = "barcode";
    private static final String DESCRIPTION_COLUMN = "description";
    private static final String DESCRIPTION_COLUMN_VALUE_FIELD_NAME = "description";
    private static final String CATEGORY_CATEGORY_TITLE = "category";
    private static final String CATEGORY_COLUMN_VALUE_FIELD_NAME = "category";
    private static final String DELETE_COLUMN_TITLE = "deleteColumn";
    private static final String[] STATUS = {"YES", "NO"};
    private static final double STATUS_YES_RED_COLOR = 0.54;
    private static final double STATUS_YES_GREEN_COLOR = 0.67;
    private static final double STATUS_YES_BLUE_COLOR = 0.09;
    private static final double STATUS_NO_RED_COLOR = 0.69;
    private static final double STATUS_NO_GREEN_COLOR = 0.047;
    private static final double STATUS_NO_BLUE_COLOR = 0.18;

    @FXML
    private Pane mainContentAnchor;
    private TableView genericTable;
    private ProductService productService;
    private CategoryService categoryService;

    public ManageProductController(StageManager stageManager) {
        super(stageManager);
    }

    @Autowired
    @Lazy
    public ManageProductController(StageManager stageManager, ProductService productService, CategoryService categoryService) {
        super(stageManager);
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @Override
    public void initialize() {
        this.createTable();
        this.getDbData();
        super.addButtonAction(this.genericTable);
    }

     @Override
     void createTable() {

        this.genericTable = new TableView();
        this.genericTable.getStyleClass().addAll(GENERIC_TABLE_STYLE_CLASS_NAME);
        this.genericTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

         double columnWidth = super.calculateColumnWidth(OBJECT_COUNT_PROPERTIES);

        /*
         * create Table columns
         */
        TableColumn<Product, Boolean> editButtonColumn = new TableColumn<>();
        setButtonColumnProperties(editButtonColumn, EDIT_COLUMN_TITLE);
        editButtonColumn.setCellFactory(param -> {
            EditButtonCell editButton = new EditButtonCell();
            editButton.createButton(genericTable, ManageProductController.super.getStageManager());
            return editButton;
        });


        TableColumn<Product, String> nameColumn = new TableColumn<>(NAME_COLUMN_TITLE);
        setColumnProperties(nameColumn, columnWidth);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setCellValueFactory(new PropertyValueFactory<>(NAME_COLUMN_VALUE_FIELD_NAME));

        TableColumn<Product, Double> priceColumn = new TableColumn<>(PRICE_COLUMN_TITLE);
        setColumnProperties(priceColumn, columnWidth);
        priceColumn.setCellFactory(ac-> new TableCell<Product, Double>(){
             @Override
             protected  void updateItem(Double item, boolean empty){
                 super.updateItem(item, empty);
                 if (empty){
                     setText("");
                 } else {
                     setText(String.format("$ %.2f", item ));
                 }
             }
         });
        priceColumn.setCellValueFactory(new PropertyValueFactory<>(PRICE_COLUMN_VALUE_FIELD_NAME));

         TableColumn<Product, Double> costColumn = new TableColumn<>(COST_NAME_COLUMN);
         setColumnProperties(costColumn, columnWidth);
         costColumn.setCellFactory(ac-> new TableCell<Product, Double>(){
             @Override
             protected  void updateItem(Double item, boolean empty){
                 super.updateItem(item, empty);
                 if (empty){
                     setText("");
                 } else {
                     setText(String.format("$ %.2f", item ));
                 }
             }
         });
         costColumn.setCellValueFactory(new PropertyValueFactory<>(COST_COLUMN_VALUE_FIELD_NAME));

         TableColumn<Product, Integer> stockQuantityColumn = new TableColumn<>(IN_STOCK_COLUMN_TITLE);
         setColumnProperties(stockQuantityColumn, columnWidth);
         stockQuantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
         stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>(IN_STOCK_COLUMN_VALUE_FIELD_NAME));

        TableColumn<Product, String> imagePathColumn = new TableColumn<>(IMAGE_COLUMN_TITLE);
        setColumnProperties(imagePathColumn, columnWidth);
        imagePathColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        imagePathColumn.setCellValueFactory(new PropertyValueFactory<>(IMAGE_COLUMN_VALUE_FIELD_NAME));

        TableColumn<Product, String> barcodeColumn = new TableColumn<>(BARCODE_COLUMN_TITLE);
        setColumnProperties(barcodeColumn, columnWidth);
        barcodeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        barcodeColumn.setCellValueFactory(new PropertyValueFactory<>(BARCODE_COLUMN_VALUE_FIELD_NAME));

        TableColumn<Product, String> descriptionColumn = new TableColumn<>(DESCRIPTION_COLUMN);
        setColumnProperties(descriptionColumn, columnWidth);
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>(DESCRIPTION_COLUMN_VALUE_FIELD_NAME));

        TableColumn<Product, Boolean> availableColumn = new TableColumn<>(AVAILABLE_COLUMN_TITLE);
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
                        setText(STATUS[0]);
                        setTextFill(Color.color(STATUS_YES_RED_COLOR, STATUS_YES_GREEN_COLOR, STATUS_YES_BLUE_COLOR));
                    } else{
                        setText(STATUS[1]);
                        setTextFill(Color.color(STATUS_NO_RED_COLOR, STATUS_NO_GREEN_COLOR, STATUS_NO_BLUE_COLOR) );
                    }

                }
            }
        });
        availableColumn.setCellValueFactory(new PropertyValueFactory<>(AVAILABLE_COLUMN_VALUE_FIELD_NAME));

        TableColumn<Product, Category> categoryColumn = new TableColumn<>(CATEGORY_CATEGORY_TITLE);
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
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>(CATEGORY_COLUMN_VALUE_FIELD_NAME));

        TableColumn<Product, Boolean> deleteButtonColumn = new TableColumn<>();
        setButtonColumnProperties(deleteButtonColumn, DELETE_COLUMN_TITLE);
        deleteButtonColumn.setCellFactory(param -> {

            DeleteButtonCell deleteButton = new DeleteButtonCell();
            deleteButton.createButton(genericTable, ManageProductController.super.getStageManager());
            return deleteButton;
        });

        this.genericTable.getColumns().addAll(editButtonColumn, nameColumn, priceColumn, costColumn, stockQuantityColumn, imagePathColumn, barcodeColumn, descriptionColumn, availableColumn, categoryColumn, deleteButtonColumn);
         super.getMainContentAnchor().getChildren().add(this.genericTable);

    }

    private void getDbData(){
        if (super.getStageManager().getSearchResults().size()!=0){
            this.genericTable.setItems(this.invokeBySearch(super.getStageManager().getSearchResults()));
            super.getStageManager().getSearchResults().clear();
        }  else {
            ObservableList<Product> availableProducts =  FXCollections.observableArrayList(this.productService.getAllProductsDesc());
            this.genericTable.setItems(availableProducts);
        }
    }

    private <S> ObservableList<Product> invokeBySearch(List<S> searchResults){
        List<Product> availableProducts = (List<Product>) searchResults;
        return FXCollections.observableArrayList(availableProducts);
    }



}
