package app.controllers.manager.manager_dialogs;

import app.entities.Category;
import app.entities.Product;
import app.services.api.CategoryService;
import app.services.api.FieldValidationService;
import app.services.api.ImageUploadService;
import app.services.api.ProductService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Component
public class ProductEditDialogController implements ManagerDialogController {

    private static final int MAX_ALLOWED_DIGITS_FOR_PRICE = 9;
    private static final int BARCODE_MAX_ALLOWED_NUMBERS = 15;
    private static final String IMG_PATH_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    private static final String SYSTEM_DIR = "user.dir";


    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField costField;
    @FXML private Label imagePathLabel;
    @FXML private TextField barcodeField;
    @FXML private TextField descriptionField;
    @FXML private TextField availableField;
    @FXML private ComboBox categoryComboBox;
    @FXML private Button fileChooserButton;

    private Product product;
    private Stage stage;
    private File sourceFile;
    private ImageUploadService imageUploadService;
    private TableView table;
    private CategoryService categoryService;
    private FieldValidationService fieldValidationService;
    private ProductService productService;

    @Autowired
    public ProductEditDialogController(CategoryService categoryService, ImageUploadService imageUploadService, FieldValidationService fieldValidationService, ProductService productService) {
        this.categoryService = categoryService;
        this.imageUploadService = imageUploadService;
        this.fieldValidationService = fieldValidationService;
        this.productService = productService;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void setDialogStage(Stage dialogStage) {
        this.stage = dialogStage;
    }

    @Override
    public void setTableView(TableView tableView){
        this.table = tableView;
    }

    @Override
    public <S> void setEditObject(S product) {
        this.product = (Product)product;
        titleLabel.setText(this.stage.getTitle());

        switch(this.stage.getTitle()){
            case "Delete":
                break;
            case "Edit":
                titleLabel.setText("Edit");
                nameField.setText(this.product.getName());
                priceField.setText(String.valueOf(this.product.getPrice()));
                costField.setText(String.valueOf(this.product.getCost()));
                imagePathLabel.setText(this.product.getImagePath());
                barcodeField.setText(this.product.getBarcode());
                descriptionField.setText(this.product.getDescription());
                availableField.setText(this.product.getAvailable()?"YES":"NO");
                addCategoryChoices();
                break;
            default:
                titleLabel.setText("Add");
                addCategoryChoices();
                break;
        }
    }

    @Override
    public boolean isInputValid() {

        StringBuilder errorMessage = new StringBuilder();

        errorMessage.append(fieldValidationService.nameValidation(nameField.getText()));
        errorMessage.append(fieldValidationService.priceValidation(priceField.getText()));
        errorMessage.append(fieldValidationService.priceValidation(costField.getText()));
        errorMessage.append(fieldValidationService.barcodeValidation(barcodeField.getText()));
        errorMessage.append(fieldValidationService.availableValidation(availableField.getText()));
        errorMessage.append(fieldValidationService.categoryValidation(categoryComboBox.getItems()));

        if (errorMessage.length() <=0 && this.stage.getTitle().equalsIgnoreCase("Add")){
            List<Product> allProducts = this.productService.getAllProducts();
            for (Product product:allProducts) {
                if (product.getName().equalsIgnoreCase(nameField.getText())){
                    errorMessage.append("The category exists. No override allowed!");
                }
            }
        }


        return this.fieldValidationService.validationErrorAlertBox(errorMessage, this.stage);
    }

    @Override
    public void removeObjectFromDB(Object object){
        Product product = (Product) object;
        this.productService.removeProduct(product);
    }

    public void addFileChooser(){
        this.sourceFile = this.imageUploadService.addFileChooser(this.stage);
        if (null != sourceFile){
            imagePathLabel.setText(this.sourceFile.getName());
        }
    }

    private void addCategoryChoices() {
        //for DB
        List<Category> allCategories = categoryService.getAllCategories();

        //for dev
        //List<Category> allCategories = getAllFakeCategories();

        if (null != allCategories){
            categoryComboBox.getItems().addAll(allCategories);
            if (null != this.product){
                categoryComboBox.getSelectionModel().select(product.getCategory());
            } else {
                categoryComboBox.getSelectionModel().selectFirst();
            }
        }
    }


//    ///////////////////////// dev creating fake database entries ////////////////////////////////
//    protected <S> List<S> getAllFakeCategories(){
//        List<S> categories = new ArrayList<>();
//        String[] fakeCategories = {"coffee", "beer", "cocktails", "wine", "whiskey", "soft-drink", "brandy", "water", "tea", "bokra", "nuts", "bacon", "glo", "blo", "mlo"};
//        Long id =1L;
//
//        for (String category:fakeCategories) {
//            Category newCat = new Category();
//            newCat.setId(id);
//            newCat.setName(category);
//            categories.add((S) newCat);
//            id++;
//        }
//        return categories;
//    }
//    /////////////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    private void handleOk() {
        if (isInputValid()) {

            //getting old image name for upload check
            String oldImageName = null == this.product ? "" : this.product.getImagePath();

            if (null == this.product){
                this.product = new Product();
            }

            //uploading file
            boolean isUploaded = this.imageUploadService.uploadFile(this.sourceFile);
            if (!isUploaded){
                imagePathLabel.setText(oldImageName);
            }

            this.product.setName(nameField.getText());
            this.product.setPrice(Double.parseDouble(priceField.getText()));
            this.product.setCost(Double.parseDouble(costField.getText()));
            this.product.setImagePath(imagePathLabel.getText());
            this.product.setBarcode(barcodeField.getText());
            this.product.setDescription(descriptionField.getText());
            this.product.setAvailable(availableField.getText().equalsIgnoreCase("YES"));
            this.product.setCategory((Category) categoryComboBox.getSelectionModel().getSelectedItem());

            if (titleLabel.getText().equals("Add")){
                this.table.getItems().add(0, product);
            }

            this.productService.save(this.product);

            this.table.refresh();
            stage.close();
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

}
