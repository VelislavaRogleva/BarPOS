package app.services.impl;

import app.entities.Category;
import app.entities.Product;
import app.entities.Role;
import app.entities.User;
import app.services.api.FieldValidationService;
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

    public FieldValidationServiceImpl() {
        this.errorMessage = new StringBuilder();
    }


    @Override
    public StringBuilder nameTypeValidation(String fieldData, String fieldLabel){
        this.errorMessage.setLength(0);
        //valid names must contains only letters, numbers, one or zero space and one or zero hyphen
        if (fieldData == null || fieldData.length() == 0) {
            this.errorMessage.append(String.format("%s must not be empty!\r\n", fieldLabel));
        }
        if (!fieldData.matches("^[A-Za-z0-9]+[ -]?[A-Za-z0-9]*$")){
            this.errorMessage.append(String.format("%s must contain only letters, digits, zero or one space or hyphen!\r\n", fieldLabel));
        }
        return this.errorMessage;
    }

    @Override
    public StringBuilder priceTypeValidation(String fieldData, String fieldLabel){
        this.errorMessage.setLength(0);
        //validate price
        if (fieldData == null || fieldData.length() == 0) {
            this.errorMessage.append(String.format("%s must contain at least one digit!\r\n",fieldLabel));
        }
        if(fieldData.length() > MAX_ALLOWED_DIGITS_FOR_PRICE){
            this.errorMessage.append(String.format("%s must be less than %s digits\r\n",fieldLabel, MAX_ALLOWED_DIGITS_FOR_PRICE));
        } else   {
            try {
                Double.parseDouble(fieldData);
                BigDecimal priceBigDecimal = new BigDecimal("0.00000000000000000001");
                if((priceBigDecimal.compareTo(BigDecimal.ZERO) == 0)){
                    this.errorMessage.append(String.format("%s must not be 0\r\n", fieldLabel));
                }
            } catch(Exception e) {
                this.errorMessage.append(String.format("%s must contain only digits separated by dot e.g 1.02\r\n", fieldLabel));
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
    public StringBuilder integerTypeValidation(String fieldData, String fieldLabel, int maxAllowedNumbers){
        this.errorMessage.setLength(0);
        //validate barcode
        if (fieldData == null || fieldData.length() == 0){
            this.errorMessage.append(String.format("%s must not be empty!\r\n",fieldLabel));
        }
        if (!fieldData.matches("\\d+")){
            this.errorMessage.append(String.format("%s must contains only digits!\r\n",fieldLabel));
        }
        if (fieldData.length() > maxAllowedNumbers){
            errorMessage.append(String.format("%s must have less than %s!\r\n",fieldLabel, maxAllowedNumbers));
        }
        return this.errorMessage;
    }

    @Override
    public StringBuilder booleanTypeValidation(String fieldData, String fieldLabel, String trueValue, String falseValue){
        this.errorMessage.setLength(0);
        //check available
        if (fieldData == null || fieldData.length() == 0){
            this.errorMessage.append(String.format("%s must not be empty!\r\n",fieldLabel));
        }

        if (!fieldData.equalsIgnoreCase(trueValue) && !fieldData.equalsIgnoreCase(falseValue) ) {
            this.errorMessage.append(String.format("%s must be %s or %s!\r\n",fieldLabel, trueValue, falseValue));
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
    public  StringBuilder categoryNameMatchValidation(List<Category> categoryItems, String fieldData){
        for (Category category:categoryItems) {
            if (category.getName().equalsIgnoreCase(fieldData)){
                this.errorMessage.append("Category name is already taken!");
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
