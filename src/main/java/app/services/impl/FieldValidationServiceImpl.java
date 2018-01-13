package app.services.impl;

import app.entities.*;
import app.services.api.*;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class FieldValidationServiceImpl implements FieldValidationService {

    private static final int MAX_ALLOWED_DIGITS_FOR_PRICE = 9;
    private static final String IMG_PATH_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";

    private StringBuilder errorMessage;
    private CategoryService categoryService;
    private BarTableService barTableService;
    private ProductService productService;
    private UserService userService;

    public FieldValidationServiceImpl(CategoryService categoryService, BarTableService barTableService, ProductService productService, UserService userService) {
        this.categoryService = categoryService;
        this.barTableService = barTableService;
        this.productService = productService;
        this.userService = userService;
        this.errorMessage = new StringBuilder();
    }


    @Override
    public String nameTypeValidation(String fieldData, int maxAllowedCharactersPerWord, int nameMaxAllowedRows){
        this.errorMessage.setLength(0);

        if (fieldData == null || fieldData.length() == 0) {
            this.errorMessage.append("*must not be empty!\r\n");
        }
        try {
            String[] words = fieldData.split("\\s+");
            for (String word : words) {
                if (word.length() > maxAllowedCharactersPerWord) {
                    this.errorMessage.append(String.format("*each word must be less than %d characters!\r\n", maxAllowedCharactersPerWord));
                    break;
                }
            }

            if (fieldData.length() > maxAllowedCharactersPerWord * nameMaxAllowedRows){
                this.errorMessage.append(String.format("*field must have less than %d characters total!\r\n", maxAllowedCharactersPerWord * nameMaxAllowedRows));
            }
        } catch(Exception e){
            this.errorMessage.append("*field must not be empty!\r\n");
        }



        return this.errorMessage.toString();
    }

    @Override
    public StringBuilder priceTypeValidation(String fieldData){
        this.errorMessage.setLength(0);
        //validate price
        if (null == fieldData || fieldData.length() == 0) {
            this.errorMessage.append("*must contain at least one digit!\r\n");
        }
        if(null != fieldData && fieldData.length() > MAX_ALLOWED_DIGITS_FOR_PRICE){
            this.errorMessage.append(String.format("*must be less than %s digits\r\n", MAX_ALLOWED_DIGITS_FOR_PRICE));
        } else if( null != fieldData )   {
            try {
                double fieldPrice = Double.parseDouble(fieldData);
                if(fieldPrice>= - 0.009 && fieldPrice <= 0.009 ){
                    this.errorMessage.append("*field must not be 0!\r\n");
                }
                if(fieldPrice < 0.0){
                    this.errorMessage.append("*field must not be negative!\r\n");
                }
            } catch(Exception e) {
                this.errorMessage.append("*must contain only digits separated by dot!\r\n");
            }
        }
        return this.errorMessage;
    }

//    private void isImageValid(String textData){
//        //validate image path
//        if (null != this.sourceFile){
//            if( !textData.isEmpty() && !textData.matches(IMG_PATH_PATTERN)) {
//                errorMessage.append("Image path is incorrect\r\n");
//            }
//        }
//    }

    @Override
    public StringBuilder integerTypeValidation(String fieldData,  int maxAllowedNumbers){
        this.errorMessage.setLength(0);

        if (fieldData == null || fieldData.length() == 0){
            this.errorMessage.append("*must not be empty!\r\n");
        }
        if (fieldData != null && !fieldData.matches("\\d+")){
            this.errorMessage.append("*must contains only digits!\r\n");
        }
        if (fieldData != null && fieldData.length() > maxAllowedNumbers){
            errorMessage.append(String.format("*must have less than %s characters!\r\n", maxAllowedNumbers));
        }
        return this.errorMessage;
    }

    @Override
    public <S> StringBuilder categoryPresenceValidation(ObservableList<S> listItems){
        this.errorMessage.setLength(0);
        //check available
        if (listItems.size() == 0){
            this.errorMessage.append("No category found. Add category first!\r\n");
        }
        return this.errorMessage;
    }

    @Override
    public  StringBuilder categoryNameMatchValidation(String fieldData,Long currentCategoryId){
        this.errorMessage.setLength(0);
        List<Category> categoryItems = this.categoryService.getAllCategories();
        for (Category category:categoryItems) {
            if ((currentCategoryId == 0 && category.getName().equalsIgnoreCase(fieldData)) ||
                    (category.getName().equalsIgnoreCase(fieldData) && (currentCategoryId > category.getId() || currentCategoryId < category.getId()) ) ){
                this.errorMessage.append("*category name is already taken!");
                break;
            }
        }
        return  this.errorMessage;
    }

    @Override
    public  StringBuilder barTableNameMatchValidation(int fieldData,Long currentBarTableId){
        this.errorMessage.setLength(0);
        List<BarTable> barTableItems = this.barTableService.getAllBarTables();
        for (BarTable barTable:barTableItems) {
            if ((currentBarTableId == 0 && barTable.getNumber() == fieldData) ||
                    (barTable.getNumber() == fieldData && (currentBarTableId > barTable.getId() || currentBarTableId < barTable.getId()) ) ){
                this.errorMessage.append("*table number is already taken!");
                break;
            }
        }
        return  this.errorMessage;
    }

    @Override
    public  StringBuilder productNameMatchValidation(String fieldData, Long currentProductId){
        this.errorMessage.setLength(0);
        List<Product> productItems = this.productService.getAllProducts();
        for (Product product: productItems) {
            if ((currentProductId == 0 && product.getName().equalsIgnoreCase(fieldData)) ||
                    (product.getName().equalsIgnoreCase(fieldData) && (currentProductId > product.getId() || currentProductId < product.getId()) ) ){
                this.errorMessage.append("*product name is already taken!");
                break;
            }
        }
        return  this.errorMessage;
    }

    @Override
    public  StringBuilder barcodeMatchValidation(String fieldData, Long currentProductId){
        this.errorMessage.setLength(0);
        List<Product> productItems = this.productService.getAllProducts();
        for (Product product: productItems) {
            if ((currentProductId == 0 && product.getBarcode().equalsIgnoreCase(fieldData)) ||
                    (product.getBarcode().equalsIgnoreCase(fieldData) && (currentProductId > product.getId() || currentProductId < product.getId()) ) ){
                this.errorMessage.append("*barcode is already taken!");
                break;
            }
        }
        return  this.errorMessage;
    }

    @Override
    public  StringBuilder userNameMatchValidation(String fieldData, Long currentUserId){
        this.errorMessage.setLength(0);
        List<User> userItems = this.userService.getAllRegisteredUsers();
        for (User user: userItems) {
            if ((currentUserId == 0 && user.getName().equalsIgnoreCase(fieldData)) ||
                    (user.getName().equalsIgnoreCase(fieldData) && (currentUserId > user.getId() || currentUserId < user.getId()) ) ){
                this.errorMessage.append("*user is already taken!");
                break;
            }
        }
        return  this.errorMessage;
    }
    @Override
    public boolean validationErrorAlertBox(String errorMessage, Stage stage){
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}
