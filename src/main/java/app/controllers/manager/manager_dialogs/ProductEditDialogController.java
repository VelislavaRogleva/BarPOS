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
import javafx.scene.layout.Region;
import javafx.scene.text.TextFlow;
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
    private static final String[] AVAILABLE = {"YES", "NO"};

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextFlow nameTextFlow;
    @FXML private Label nameFieldError;
    @FXML private TextField priceField;
    @FXML private TextFlow priceTextFlow;
    @FXML private Label priceFieldError;
    @FXML private TextField costField;
    @FXML private TextFlow costTextFlow;
    @FXML private Label costFieldError;
    @FXML private TextField inStockField;
    @FXML private TextFlow inStockTextFlow;
    @FXML private Label inStockFieldError;
    @FXML private Label imagePathLabel;
    @FXML private TextField barcodeField;
    @FXML private TextFlow barcodeTextFlow;
    @FXML private Label barcodeFieldError;
    @FXML private TextField descriptionField;
    @FXML private ComboBox<String> availableComboBox;
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
    private boolean isValid;

    @Autowired
    public ProductEditDialogController(CategoryService categoryService, ImageUploadService imageUploadService, FieldValidationService fieldValidationService, ProductService productService) {
        this.categoryService = categoryService;
        this.imageUploadService = imageUploadService;
        this.fieldValidationService = fieldValidationService;
        this.productService = productService;
    }

    @Override
    public void initialize() {
        this.isValid = true;
        this.hideErrorTextFlowContainer(this.nameTextFlow);
        this.hideErrorTextFlowContainer(this.priceTextFlow);
        this.hideErrorTextFlowContainer(this.costTextFlow);
        this.hideErrorTextFlowContainer(this.inStockTextFlow);
        this.hideErrorTextFlowContainer(this.barcodeTextFlow);
        this.nameValidationListener();
        this.priceValidationListener();
        this.costValidationListener();
        this.inStockValidationListener();
        this.barcodeValidationListener();
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
                addAvailableChoices();
                addCategoryChoices();
                break;
            default:
                titleLabel.setText("Add");
                addAvailableChoices();
                addCategoryChoices();
                break;
        }
    }

    @Override
    public boolean isInputValid() {

        StringBuilder errorMessage = new StringBuilder();

        if (this.isValid){
            errorMessage.append(fieldValidationService.categoryPresenceValidation(categoryComboBox.getItems()));
            return this.fieldValidationService.validationErrorAlertBox(errorMessage.toString(), this.stage);
        }
        return false;


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
            this.categoryComboBox.getItems().addAll(allCategories);
            if (null != this.product){
                this.categoryComboBox.getSelectionModel().select(this.product.getCategory());
            } else {
                this.categoryComboBox.getSelectionModel().selectFirst();
            }
        }
    }

    private void addAvailableChoices() {
        availableComboBox.getItems().addAll(AVAILABLE);
        if (null != this.product){
            availableComboBox.getSelectionModel().select(this.product.getAvailable()?"YES":"NO");
        } else {
            this.availableComboBox.getSelectionModel().selectFirst();
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
                if ( null != imageName && ( !imageName.isEmpty() || !imageName.equals(oldImageName))) {
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
                this.product.setAvailable(availableComboBox.getValue().equalsIgnoreCase("YES"));
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


    private void nameValidationListener(){
        if (null != this.nameField) {
            this.nameField.setOnKeyTyped(event -> {
                this.errorFieldDefault(nameTextFlow, nameFieldError);
            });
            this.nameField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
                if (!newValue && isValid) {
                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append(this.fieldValidationService.nameTypeValidation(this.nameField.getText(), 20, 1));
                    //check if name exist
                    Long currentProductId = null == this.product ? 0 : this.product.getId();
                    if (errorMessage.length() <= 0){
                        errorMessage.append(this.fieldValidationService.productNameMatchValidation(this.nameField.getText(), currentProductId));
                    }

                    //TODO
                    //product in open order must not be modified


                    this.errorResultHandler(errorMessage, this.nameField, this.nameTextFlow, this.nameFieldError);
                }

            });
        }
    }

    private void priceValidationListener(){
        if (null != this.priceField) {
            this.priceField.setOnKeyTyped(event -> {
                this.errorFieldDefault(priceTextFlow, priceFieldError);
            });
            this.priceField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
                if (!newValue && isValid) {
                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append(fieldValidationService.priceTypeValidation(priceField.getText()));

                    this.errorResultHandler(errorMessage, this.priceField, this.priceTextFlow, this.priceFieldError);
                }
            });
        }
    }

    private void costValidationListener(){
        if (null != this.costField) {
            this.costField.setOnKeyTyped(event -> {
                this.errorFieldDefault(this.costTextFlow, this.costFieldError);
            });
            this.costField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
                if (!newValue && isValid) {
                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append(fieldValidationService.priceTypeValidation(costField.getText()));

                    this.errorResultHandler(errorMessage, this.costField, this.costTextFlow, this.costFieldError);
                }
            });
        }
    }

    private void inStockValidationListener(){
        if (null != this.inStockField) {
            this.inStockField.setOnKeyTyped(event -> {
                this.errorFieldDefault(this.inStockTextFlow, this.inStockFieldError);
            });
            this.inStockField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
                if (!newValue && isValid) {
                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append(fieldValidationService.integerTypeValidation(inStockField.getText(), STOCK_MAX_ALLOWED_UNITS));

                    this.errorResultHandler(errorMessage, this.inStockField, this.inStockTextFlow, this.inStockFieldError);
                }
            });
        }
    }

    private void barcodeValidationListener(){
        if (null != this.barcodeField) {
            this.barcodeField.setOnKeyTyped(event -> {
                this.errorFieldDefault(this.barcodeTextFlow, this.barcodeFieldError);
            });
            this.barcodeField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
                if (!newValue && isValid) {
                    StringBuilder errorMessage = new StringBuilder();

                    errorMessage.append(fieldValidationService.integerTypeValidation(barcodeField.getText(), BARCODE_MAX_ALLOWED_NUMBERS));
                    //check if name exist
                    Long currentProductId = null == this.product ? 0 : this.product.getId();
                    if (errorMessage.length() <= 0){
                        errorMessage.append(this.fieldValidationService.barcodeMatchValidation(this.barcodeField.getText(), currentProductId));
                    }

                    this.errorResultHandler(errorMessage, this.barcodeField, this.barcodeTextFlow, this.barcodeFieldError);
                }

            });
        }
    }


    private void hideErrorTextFlowContainer(TextFlow textFlow){
        textFlow.setVisible(false);
        textFlow.setPrefHeight(0.0);
    }

    private void showErrorTextFlowContainer(TextFlow textFlow){
        textFlow.setVisible(true);
        textFlow.setPrefHeight(Region.USE_COMPUTED_SIZE);
    }

    private void errorFieldDefault(TextFlow textFlow, Label errorLabel ){
        if (!errorLabel.getText().isEmpty()){
            this.isValid = true;
        }
        errorLabel.setText("");
        this.hideErrorTextFlowContainer(textFlow);
    }

    private void errorResultHandler(StringBuilder errorMessage, TextField textField, TextFlow textFlow, Label errorLabel){
        if (errorMessage.length() > 0) {
            textField.setStyle("-fx-border-color: firebrick");
            errorLabel.setText(errorMessage.toString());
            this.showErrorTextFlowContainer(textFlow);
            this.isValid = false;
        } else {
            textField.setStyle("-fx-border-color: transparent");
            this.hideErrorTextFlowContainer(textFlow);
            this.isValid = true;
        }
    }

}
