package app.controllers.manager.manager_elements;

import app.controllers.manager.crud_buttons.DeleteButtonCell;
import app.controllers.manager.crud_buttons.EditButtonCell;
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
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class ManageProductController extends BaseManageController {

    private static final int OBJECT_COUNT_PROPERTIES = 9;

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
        this.genericTable.getStyleClass().addAll("contentTable");
        this.genericTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

         double columnWidth = super.calculateColumnWidth(OBJECT_COUNT_PROPERTIES);

        /*
         * create Table columns
         */
        TableColumn<Product, Boolean> editButtonColumn = new TableColumn<>();
        setButtonColumnProperties(editButtonColumn, "editColumn");
        editButtonColumn.setCellFactory(new Callback<TableColumn<Product, Boolean>, TableCell<Product, Boolean>>() {
            @Override
            public TableCell<Product, Boolean> call(TableColumn<Product, Boolean> param) {
                EditButtonCell editButton = new EditButtonCell(ManageProductController.super.getStageManager());
                editButton.createButton(genericTable);
                return editButton;
            }
        });


        TableColumn<Product, String> nameColumn = new TableColumn<>("name");
        setColumnProperties(nameColumn, columnWidth);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("price");
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
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

         TableColumn<Product, Double> costColumn = new TableColumn<>("cost");
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
         costColumn.setCellValueFactory(new PropertyValueFactory<>("cost"));

         TableColumn<Product, Integer> stockQuantityColumn = new TableColumn<>("in stock");
         setColumnProperties(stockQuantityColumn, columnWidth);
         stockQuantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
         stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        TableColumn<Product, String> imagePathColumn = new TableColumn<>("image");
        setColumnProperties(imagePathColumn, columnWidth);
        imagePathColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        imagePathColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));

        TableColumn<Product, String> barcodeColumn = new TableColumn<>("barcode");
        setColumnProperties(barcodeColumn, columnWidth);
        barcodeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        barcodeColumn.setCellValueFactory(new PropertyValueFactory<>("barcode"));

        TableColumn<Product, String> descriptionColumn = new TableColumn<>("description");
        setColumnProperties(descriptionColumn, columnWidth);
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

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
        deleteButtonColumn.setCellFactory(param -> {
            DeleteButtonCell deleteButton = new DeleteButtonCell(ManageProductController.super.getStageManager());
            deleteButton.createButton(genericTable);
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
