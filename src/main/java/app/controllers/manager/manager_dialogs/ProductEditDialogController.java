package app.controllers.manager.manager_dialogs;

import app.entities.Category;
import app.entities.Product;
import app.services.api.CategoryService;
import app.services.api.FieldValidationService;
import app.services.api.ImageUploadService;
import app.services.api.ProductService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Component
public class ProductEditDialogController implements ManagerDialogController {

    private static final int BARCODE_MAX_ALLOWED_NUMBERS = 15;
    private static final int STOCK_MAX_ALLOWED_UNITS = 500;

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField costField;
    @FXML private TextField inStockField;
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
    private int selectedIndex;

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
    public void setSelectedIndex(int index){
        this.selectedIndex = index;
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
                inStockField.setText(String.valueOf(this.product.getStockQuantity()));
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

        errorMessage.append(fieldValidationService.nameTypeValidation(nameField.getText(), nameField.getPromptText()));
        errorMessage.append(fieldValidationService.priceTypeValidation(priceField.getText(), priceField.getPromptText()));
        errorMessage.append(fieldValidationService.priceTypeValidation(costField.getText(), costField.getPromptText()));
        errorMessage.append(fieldValidationService.integerTypeValidation(inStockField.getText(), inStockField.getPromptText(), STOCK_MAX_ALLOWED_UNITS));
        errorMessage.append(fieldValidationService.integerTypeValidation(barcodeField.getText(), barcodeField.getPromptText(), BARCODE_MAX_ALLOWED_NUMBERS));
        errorMessage.append(fieldValidationService.booleanTypeValidation(availableField.getText(), availableField.getPromptText(), "YES", "NO"));
        errorMessage.append(fieldValidationService.categoryPresenceValidation(categoryComboBox.getItems()));

//        if (errorMessage.length() <=0){
//            List<Product> allProducts = this.productService.getAllProducts();
//            for (Product product:allProducts) {
//                if( (product.getName().equalsIgnoreCase(nameField.getText()) && this.stage.getTitle().equalsIgnoreCase("Add") ) ||
//                        product.getName().equalsIgnoreCase(nameField.getText()) && ( (product.getId() > this.product.getId()) || (product.getId() < this.product.getId()) )  )
//                    {
//                    errorMessage.append("This product name exists. No override allowed!");
//                    break;
//                }
//                if( (product.getBarcode().equalsIgnoreCase(barcodeField.getText()) && this.stage.getTitle().equalsIgnoreCase("Add") ) ||
//                        product.getBarcode().equalsIgnoreCase(barcodeField.getText()) && ( (product.getId() > this.product.getId()) || (product.getId() < this.product.getId()) )  )
//                {
//                    errorMessage.append("This barcode exists. No override allowed!");
//                    break;
//                }
//            }
//        }


        return this.fieldValidationService.validationErrorAlertBox(errorMessage.toString(), this.stage);
    }

    private <S> void removeObjectFromDB(){
        this.productService.removeProduct(this.product);
  //      this.table.getItems().remove(this.selectedIndex);
    }

    public void addFileChooser(){
        this.sourceFile = this.imageUploadService.addFileChooser(this.stage);
        if (null != sourceFile){
            imagePathLabel.setText(this.sourceFile.getName());
        }
    }

    private void addCategoryChoices() {
        List<Category> allCategories = categoryService.getAllCategories();
        if (null != allCategories){
            categoryComboBox.getItems().addAll(allCategories);
            if (null != this.product){
                categoryComboBox.getSelectionModel().select(product.getCategory());
            } else {
                categoryComboBox.getSelectionModel().selectFirst();
            }
        }
    }

    @FXML
    private void handleOk() {
        try{
            if (this.stage.getTitle().equalsIgnoreCase("Delete")){
                removeObjectFromDB();
                stage.close();
            } else if (isInputValid()) {

                if (null == this.product){
                    this.product = new Product();
                }

                //getting old image name for upload check
                String oldImageName = null == this.product ? "" : this.product.getImagePath();

                //uploading file
                String imageName = imagePathLabel.getText();
                if (!imageName.isEmpty() || imageName.equals(oldImageName)){
                    boolean isUploaded = this.imageUploadService.uploadFile(this.sourceFile);
                    if (!isUploaded){
                        this.product.setImagePath(oldImageName);
                    }
                }



                this.product.setName(nameField.getText());
                this.product.setPrice(Double.parseDouble(priceField.getText()));
                this.product.setCost(Double.parseDouble(costField.getText()));
                this.product.setStockQuantity(Integer.parseInt(inStockField.getText()));
                this.product.setImagePath(imagePathLabel.getText());
                this.product.setBarcode(barcodeField.getText());
                this.product.setDescription(descriptionField.getText());
                this.product.setAvailable(availableField.getText().equalsIgnoreCase("YES"));
                this.product.setCategory((Category) categoryComboBox.getSelectionModel().getSelectedItem());

                this.productService.save( this.product);

                if (titleLabel.getText().equals("Add")){
                    this.table.getItems().add(0, this.product);
                }

                stage.close();
            }
        } catch (RuntimeException re){
            this.fieldValidationService.validationErrorAlertBox("This barcode exists. No override allowed!", stage);
            this.table.setItems(FXCollections.observableArrayList(this.productService.getAllProductsDesc()));
        } catch (Exception e){
            this.fieldValidationService.validationErrorAlertBox("Cannot complete action! Incorrect field value", stage);
            this.table.setItems(FXCollections.observableArrayList(this.productService.getAllProductsDesc()));
        }

    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

}
