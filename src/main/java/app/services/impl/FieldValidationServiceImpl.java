package app.services.impl;

import app.services.api.FieldValidationService;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FieldValidationServiceImpl implements FieldValidationService {

    private static final int MAX_ALLOWED_DIGITS_FOR_PRICE = 9;
    private static final int BARCODE_MAX_ALLOWED_NUMBERS = 15;
    private static final String IMG_PATH_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";

    private StringBuilder errorMessage;

    public FieldValidationServiceImpl() {
        this.errorMessage = new StringBuilder();
    }

    @Override
    public StringBuilder nameValidation(String fieldData){

        //valid names must contains only letters, numbers, one or zero space and one or zero hyphen
        if (fieldData == null || fieldData.length() == 0) {
            this.errorMessage.append("Name must not be empty!\r\n");
        }
        if (!fieldData.matches("^[A-Za-z0-9]+[ -]?[A-Za-z0-9]*$")){
            this.errorMessage.append("Name must contain only letters, digits, zero or one space or hyphen!\r\n");
        }
        return this.errorMessage;
    }

    @Override
    public StringBuilder priceValidation(String fieldData){
        //validate price
        if (fieldData == null || fieldData.length() == 0) {
            this.errorMessage.append("Price must contain at least one digit!\r\n");
        }
        if(fieldData.length() > MAX_ALLOWED_DIGITS_FOR_PRICE){
            this.errorMessage.append(String.format("Price must be less than %s digits\r\n",MAX_ALLOWED_DIGITS_FOR_PRICE));
        } else   {
            try {
                Double.parseDouble(fieldData);
                BigDecimal priceBigDecimal = new BigDecimal("0.00000000000000000001");
                if((priceBigDecimal.compareTo(BigDecimal.ZERO) == 0)){
                    this.errorMessage.append("Price must not be 0\r\n");
                }
            } catch(Exception e) {
                this.errorMessage.append("Price must contain only digits separated by dot e.g 1.02\r\n");
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
    public StringBuilder barcodeValidation(String fieldData){
        //validate barcode
        if (fieldData == null || fieldData.length() == 0){
            this.errorMessage.append("Barcode must not be empty!\r\n");
        }
        if (!fieldData.matches("\\d+")){
            this.errorMessage.append("Barcode must contains only digits!\r\n");
        }
        if (fieldData.length() > BARCODE_MAX_ALLOWED_NUMBERS){
            errorMessage.append(String.format("Barcode must have less than %s!\r\n", BARCODE_MAX_ALLOWED_NUMBERS));
        }
        return this.errorMessage;
    }

    @Override
    public StringBuilder availableValidation(String fieldData){
        //check available
        if (fieldData == null || fieldData.length() == 0){
            this.errorMessage.append("Available must not be empty!\r\n");
        }

        if (!fieldData.equalsIgnoreCase("NO") && !fieldData.equalsIgnoreCase("YES") ) {
            errorMessage.append("Available must be Yes or No!\r\n");
        }
        return this.errorMessage;
    }

    @Override
    public <S> StringBuilder categoryValidation(ObservableList<S> listItems){
        //check available
        if (listItems.size() == 0){
            this.errorMessage.append("No category found. Add category first!\r\n");
        }
        return this.errorMessage;
    }

    @Override
    public boolean validationErrorAlertBox(StringBuilder errorMessage, Stage stage){
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage.toString());

            alert.showAndWait();

            return false;
        }
    }
}
