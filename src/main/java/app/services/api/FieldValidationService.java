package app.services.api;

import javafx.collections.ObservableList;
import javafx.stage.Stage;

public interface FieldValidationService {

    StringBuilder nameValidation(String fieldData);

    StringBuilder priceValidation(String fieldData);

    StringBuilder barcodeValidation(String fieldData);

    StringBuilder availableValidation(String fieldData);

    <S> StringBuilder categoryValidation(ObservableList<S> listItem);

    boolean validationErrorAlertBox(StringBuilder errorMessage, Stage stage);
}
