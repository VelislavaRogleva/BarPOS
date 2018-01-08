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
        errorMessage.append(fieldValidationService.barcodeTypeValidation(barcodeField.getText(), barcodeField.getPromptText()));
        errorMessage.append(fieldValidationService.booleanTypeValidation(availableField.getText(), availableField.getPromptText(), "YES", "NO"));
        errorMessage.append(fieldValidationService.categoryPresenceValidation(categoryComboBox.getItems()));

        if (errorMessage.length() <=0 && this.stage.getTitle().equalsIgnoreCase("Add")){
            List<Product> allProducts = this.productService.getAllProducts();
            for (Product product:allProducts) {
                if (product.getName().equalsIgnoreCase(nameField.getText())){
                    errorMessage.append("The category exists. No override allowed!");
                    break;
                }
            }
        }

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
        if (this.stage.getTitle().equalsIgnoreCase("Delete")){
            removeObjectFromDB();
            stage.close();
        } else if (isInputValid()) {

            //getting old image name for upload check
            String oldImageName = null == this.product ? "" : this.product.getImagePath();

            if (null == this.product){
                this.product = new Product();
            }

            //uploading file
            String imageName = imagePathLabel.getText();
            if (!imageName.isEmpty() || imageName.equals(oldImageName)){
                boolean isUploaded = this.imageUploadService.uploadFile(this.sourceFile);
                if (!isUploaded){
                    imagePathLabel.setText(oldImageName);
                }
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
            stage.close();
        }

    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

}
