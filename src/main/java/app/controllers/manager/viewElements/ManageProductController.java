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
import java.text.DecimalFormat;

@Component
public class ManageProductController extends BaseManageController {

    private static final int OBJECT_COUNT_PROPERTIES = 8;

    private TableView genericTable;
    private ProductService productService;
    private CategoryService categoryService;

    @Autowired
    @Lazy
    public ManageProductController(StageManager stageManager, ProductService productService, CategoryService categoryService) {
        super(stageManager);
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @Override
    public void initialize() {
        createTable();
        addButtonAction(this.genericTable);
    }


//    ///////////////////////// dev creating fake database entries ////////////////////////////////
//    protected <S> ObservableList<S> getAllFakeCategories(){
//        ObservableList<S> categories = FXCollections.observableArrayList();
//
//        String[] fakeCategories = {"coffee", "beer", "cocktails", "wine", "whiskey", "soft-drink", "brandy", "water", "tea", "nuts", "bacon", "glo", "blo", "mlo"};
//        Long id =1L;
//        double price = 12.0;
//        String barcode = "1232132132323";
//        double cost = 5.02;
//        boolean available = true;
//
//        for (String category:fakeCategories) {
//            Product newCat = new Product();
//            newCat.setId(id);
//            newCat.setName(category);
//            newCat.setPrice(price);
//            newCat.setCost(cost);
//            newCat.setImagePath("/img/ts.png");
//            newCat.setBarcode(barcode);
//            newCat.setDescription("This is blq blq blq blq blq blq blq blq");
//            newCat.setAvailable(available);
//            Category cat =  new Category();
//            cat.setId(17L);
//            cat.setName("bokra");
//            newCat.setCategory(cat);
//            categories.add((S) newCat);
//            id++;
//            price += 105.0;
//            cost += 12.12;
//            available = !(price % 2 == 0);
//        }
//        return categories;
//    }
//    /////////////////////////////////////////////////////////////////////////////////////////////////


     @Override
     void createTable() {

        genericTable = new TableView();
        genericTable.getStyleClass().addAll("contentTable");
        genericTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

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
        nameColumn.setCellFactory(TextFieldTableCell.<Product>forTableColumn());
        nameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("price");
        setColumnProperties(priceColumn, columnWidth);
        priceColumn.setCellFactory(ac-> new TableCell<Product, Double>(){
             @Override
             protected  void updateItem(Double item, boolean empty){
                 super.updateItem(item, empty);
                 if (empty){
                     setText("$ 0.00");
                 } else {
                     setText(String.format("$ %.2f", item ));
                 }
             }
         });
        priceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));

         TableColumn<Product, Double> costColumn = new TableColumn<>("cost");
         setColumnProperties(costColumn, columnWidth);
         costColumn.setCellFactory(ac-> new TableCell<Product, Double>(){
             @Override
             protected  void updateItem(Double item, boolean empty){
                 super.updateItem(item, empty);
                 if (empty){
                     setText("$ 0.00");
                 } else {
                     setText(String.format("$ %.2f", item ));
                 }
             }
         });
         costColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("cost"));

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
                DeleteButtonCell deleteButton = new DeleteButtonCell(ManageProductController.super.getStageManager());
                deleteButton.createButton(genericTable);
                return deleteButton;
            }
        });


        this.genericTable.getColumns().addAll(editButtonColumn, nameColumn, priceColumn, costColumn, imagePathColumn, barcodeColumn, descriptionColumn, availableColumn, categoryColumn, deleteButtonColumn);


        /*
         * fetch from database
         */
         ObservableList<Product> availableProducts = FXCollections.observableArrayList(this.productService.getAllProducts());

        //populate with fake content
        //ObservableList<Product> availableProducts = getAllFakeCategories();
        if (availableProducts.size()>0) {
        //        set fake items
       //     this.genericTable.setItems(getAllFakeCategories());
            //set db items
            this.genericTable.setItems(availableProducts);
            super.getMainContentAnchor().getChildren().addAll(this.genericTable);
        }
    }
}
