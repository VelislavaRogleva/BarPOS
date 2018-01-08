package app.services.api;

import app.entities.Category;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.List;

public interface FieldValidationService {

    StringBuilder nameTypeValidation(String fieldData, String fieldLabel);

    StringBuilder priceTypeValidation(String fieldData, String fieldLabel);

    StringBuilder barcodeTypeValidation(String fieldData, String fieldLabel);

    StringBuilder booleanTypeValidation(String fieldData, String fieldLabel, String trueValue, String falseValue);

    <S> StringBuilder categoryPresenceValidation(ObservableList<S> listItem);

    StringBuilder categoryNameMatchValidation(List<Category> categoryItems, String fieldData);

    boolean validationErrorAlertBox(String errorMessage, Stage stage);
}
