package app.controllers.manager.manager_dialogs;

import app.entities.Category;
import app.entities.Product;
import app.services.api.CategoryService;
import app.services.api.ImageUploadService;
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

    @Autowired
    public ProductEditDialogController(CategoryService categoryService, ImageUploadService imageUploadService) {
        this.categoryService = categoryService;
        this.imageUploadService = imageUploadService;
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

        if (product != null){
            titleLabel.setText("Edit");
            nameField.setText(this.product.getName());
            priceField.setText(String.valueOf(this.product.getPrice()));
            imagePathLabel.setText(this.product.getImagePath());
            barcodeField.setText(this.product.getBarcode());
            descriptionField.setText(this.product.getDescription());
            availableField.setText(this.product.getAvailable()?"YES":"NO");
            addCategoryChoices();
        } else {
            titleLabel.setText("Add");
            addCategoryChoices();
        }
    }


    public void addFileChooser(){
        this.sourceFile = this.imageUploadService.addFileChooser(this.stage);
        if (null != sourceFile){
            imagePathLabel.setText(this.sourceFile.getName());
        }
    }

    private void addCategoryChoices() {
        //for DB
//        List<Category> allCategories = categoryService.getAllCategories();

        //for dev
        List<Category> allCategories = getAllFakeCategories();

        if (null != allCategories){
            categoryComboBox.getItems().addAll(allCategories);
            if (null != this.product){
                categoryComboBox.getSelectionModel().select(product.getCategory());
            } else {
                categoryComboBox.getSelectionModel().selectFirst();
            }
        }
    }


    ///////////////////////// dev creating fake database entries ////////////////////////////////
    protected <S> List<S> getAllFakeCategories(){
        List<S> categories = new ArrayList<>();
        String[] fakeCategories = {"coffee", "beer", "cocktails", "wine", "whiskey", "soft-drink", "brandy", "water", "tea", "bokra", "nuts", "bacon", "glo", "blo", "mlo"};
        Long id =1L;

        for (String category:fakeCategories) {
            Category newCat = new Category();
            newCat.setId(id);
            newCat.setName(category);
            categories.add((S) newCat);
            id++;
        }
        return categories;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

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
            this.product.setImagePath(imagePathLabel.getText());
            this.product.setBarcode(barcodeField.getText());
            this.product.setDescription(descriptionField.getText());
            this.product.setAvailable(availableField.getText().equalsIgnoreCase("YES"));
            this.product.setCategory((Category) categoryComboBox.getSelectionModel().getSelectedItem());

            if (titleLabel.getText().equals("Add")){
                this.table.getItems().add(0, product);
            }

            this.table.refresh();
            stage.close();
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

//TODO make validation service
    private boolean isInputValid() {

        StringBuilder errorMessage = new StringBuilder();

        //valid names must contains only letters, numbers, one or zero space and one or zero hyphen
        if (nameField.getText() == null || nameField.getText().length() == 0) {
            errorMessage.append("Name must not be empty!\r\n");
        }
        if (!nameField.getText().matches("^[A-Za-z0-9]+[ -]?[A-Za-z0-9]*$")){
            errorMessage.append("Name must contain only letters, digits, zero or one space or hyphen!\r\n");
        }

        //validate price
        if (priceField.getText() == null || priceField.getText().length() == 0) {
            errorMessage.append("Price must contain at least one digit!\r\n");
        }
        if(priceField.getText().length() > MAX_ALLOWED_DIGITS_FOR_PRICE){
            errorMessage.append(String.format("Price must be less than %s digits\r\n",MAX_ALLOWED_DIGITS_FOR_PRICE));
        } else   {
            try {
                Double.parseDouble(priceField.getText());
                BigDecimal priceBigDecimal = new BigDecimal("0.00000000000000000001");
                if((priceBigDecimal.compareTo(BigDecimal.ZERO) == 0)){
                    errorMessage.append("Price must not be 0\r\n");
                }
            } catch(Exception e) {
                errorMessage.append("Price must contain only digits separated by dot e.g 1.02\r\n");
            }
        }

        //validate image path
        if (null != this.sourceFile){
            if( !imagePathLabel.getText().isEmpty() && !imagePathLabel.getText().matches(IMG_PATH_PATTERN)) {
                errorMessage.append("Image path is incorrect\r\n");
            }
        }
        //validate barcode
        if (barcodeField.getText() == null || barcodeField.getText().length() == 0){
            errorMessage.append("Barcode must not be empty!\r\n");
        }
        if (!barcodeField.getText().matches("\\d+")){
            errorMessage.append("Barcode must contains only digits!\r\n");
        }
        if (barcodeField.getText().length() > BARCODE_MAX_ALLOWED_NUMBERS){
            errorMessage.append(String.format("Barcode must have less than %s!\r\n", BARCODE_MAX_ALLOWED_NUMBERS));
        }

        //check available
        if (availableField.getText() == null || availableField.getText().length() == 0){
            errorMessage.append("Available must not be empty!\r\n");
        }

        if (!availableField.getText().equalsIgnoreCase("NO") && !availableField.getText().equalsIgnoreCase("YES") ) {
            errorMessage.append("Available must be Yes or No!\r\n");
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(this.stage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage.toString());

            alert.showAndWait();

            return false;
        }
    }

}
