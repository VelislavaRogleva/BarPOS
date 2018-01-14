package app.services.impl;

import app.entities.*;
import app.services.api.*;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FieldValidationServiceImpl implements FieldValidationService {

    private static final String DIGIT_REGEX_PATTERN = "\\d+";
    private static final int MAX_ALLOWED_DIGITS_FOR_PRICE = 9;
    private static final String IMG_PATH_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    private static final String EMPTY_FIELD_ERROR = "*must not be empty!\r\n";
    private static final String NAME_WORD_MAX_LENGTH_ERROR = "*each word must be less than %d characters!\r\n";
    private static final String NAME_ALL_WORDS_MAX_LENGTH_ERROR = "*field must have less than %d characters total!\r\n";
    private static final String DIGIT_MIN_LENGTH_ERROR = "*must contain at least one digit!\r\n";
    private static final String DIGIT_MAX_LENGTH_ERROR = "*must be less than %s digits\r\n";
    private static final double ZERO_NEGATIVE_THRESHOLD = -0.009;
    private static final double ZERO_POSITIVE_THRESHOLD = 0.009;
    private static final String DIGIT_EMPTY_FIELD_ERROR = "*field must not be 0!\r\n";
    private static final String DIGIT_NEGATIVE_VALUE_ERROR = "*field must not be negative!\r\n";
    private static final String DIGIT_NOT_ALLOWED_CHARACTERS_ERROR = "*must contain only digits separated by dot!\r\n";
    private static final String NO_CATEGORY_ERROR = "No category found. Add category first!\r\n";
    private static final String CATEGORY_EXIST_ERROR = "*category name is already taken!";
    private static final int OBJECT_DEFAULT_ID = 0;
    private static final String TABLE_EXIST_ERROR = "*table number is already taken!";
    private static final String PRODUCT_EXIST_ERROR = "*product name is already taken!";
    private static final String BARCODE_EXIST_ERROR = "*barcode is already taken!";
    private static final String USER_EXIST_ERROR = "*user is already taken!";
    private static final String ALERT_BOX_TITLE = "Invalid Fields";
    private static final String ALERT_BOX_HEADER = "Please correct invalid fields";
    private static final String SPLIT_PATTERN = "\\s+";

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
            this.errorMessage.append(EMPTY_FIELD_ERROR);
        }
        try {
            String[] words = fieldData.split(SPLIT_PATTERN);
            for (String word : words) {
                if (word.length() > maxAllowedCharactersPerWord) {
                    this.errorMessage.append(String.format(NAME_WORD_MAX_LENGTH_ERROR, maxAllowedCharactersPerWord));
                    break;
                }
            }

            if (fieldData.length() > maxAllowedCharactersPerWord * nameMaxAllowedRows){
                this.errorMessage.append(String.format(NAME_ALL_WORDS_MAX_LENGTH_ERROR, maxAllowedCharactersPerWord * nameMaxAllowedRows));
            }
        } catch(Exception e){
            this.errorMessage.append(EMPTY_FIELD_ERROR);
        }



        return this.errorMessage.toString();
    }

    @Override
    public StringBuilder priceTypeValidation(String fieldData){
        this.errorMessage.setLength(0);
        //validate price
        if (null == fieldData || fieldData.length() == 0) {
            this.errorMessage.append(DIGIT_MIN_LENGTH_ERROR);
        }
        if(null != fieldData && fieldData.length() > MAX_ALLOWED_DIGITS_FOR_PRICE){
            this.errorMessage.append(String.format(DIGIT_MAX_LENGTH_ERROR, MAX_ALLOWED_DIGITS_FOR_PRICE));
        } else if( null != fieldData )   {
            try {
                double fieldPrice = Double.parseDouble(fieldData);
                if(fieldPrice>= ZERO_NEGATIVE_THRESHOLD && fieldPrice <= ZERO_POSITIVE_THRESHOLD){
                    this.errorMessage.append(DIGIT_EMPTY_FIELD_ERROR);
                }
                if(fieldPrice < 0.0){
                    this.errorMessage.append(DIGIT_NEGATIVE_VALUE_ERROR);
                }
            } catch(Exception e) {
                this.errorMessage.append(DIGIT_NOT_ALLOWED_CHARACTERS_ERROR);
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
            this.errorMessage.append(EMPTY_FIELD_ERROR);
        }
        if (fieldData != null && !fieldData.matches(DIGIT_REGEX_PATTERN)){
            this.errorMessage.append(DIGIT_NOT_ALLOWED_CHARACTERS_ERROR);
        }
        if (fieldData != null && fieldData.length() > maxAllowedNumbers){
            errorMessage.append(String.format(DIGIT_MAX_LENGTH_ERROR, maxAllowedNumbers));
        }
        return this.errorMessage;
    }

    @Override
    public <S> StringBuilder categoryPresenceValidation(ObservableList<S> listItems){
        this.errorMessage.setLength(0);
        //check available
        if (listItems.size() == 0){
            this.errorMessage.append(NO_CATEGORY_ERROR);
        }
        return this.errorMessage;
    }

    @Override
    public  StringBuilder categoryNameMatchValidation(String fieldData,Long currentCategoryId){
        this.errorMessage.setLength(0);
        List<Category> categoryItems = this.categoryService.getAllCategories();
        for (Category category:categoryItems) {
            if ((currentCategoryId == OBJECT_DEFAULT_ID && category.getName().equalsIgnoreCase(fieldData)) ||
                    (category.getName().equalsIgnoreCase(fieldData) && (currentCategoryId > category.getId() || currentCategoryId < category.getId()) ) ){
                this.errorMessage.append(CATEGORY_EXIST_ERROR);
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
            if ((currentBarTableId == OBJECT_DEFAULT_ID && barTable.getNumber() == fieldData) ||
                    (barTable.getNumber() == fieldData && (currentBarTableId > barTable.getId() || currentBarTableId < barTable.getId()) ) ){
                this.errorMessage.append(TABLE_EXIST_ERROR);
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
            if ((currentProductId == OBJECT_DEFAULT_ID && product.getName().equalsIgnoreCase(fieldData)) ||
                    (product.getName().equalsIgnoreCase(fieldData) && (currentProductId > product.getId() || currentProductId < product.getId()) ) ){
                this.errorMessage.append(PRODUCT_EXIST_ERROR);
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
            if ((currentProductId == OBJECT_DEFAULT_ID && product.getBarcode().equalsIgnoreCase(fieldData)) ||
                    (product.getBarcode().equalsIgnoreCase(fieldData) && (currentProductId > product.getId() || currentProductId < product.getId()) ) ){
                this.errorMessage.append(BARCODE_EXIST_ERROR);
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
            if ((currentUserId == OBJECT_DEFAULT_ID && user.getName().equalsIgnoreCase(fieldData)) ||
                    (user.getName().equalsIgnoreCase(fieldData) && (currentUserId > user.getId() || currentUserId < user.getId()) ) ){
                this.errorMessage.append(USER_EXIST_ERROR);
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
            alert.setTitle(ALERT_BOX_TITLE);
            alert.setHeaderText(ALERT_BOX_HEADER);
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}
