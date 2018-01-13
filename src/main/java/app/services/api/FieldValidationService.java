package app.services.api;

import app.entities.Category;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.List;

public interface FieldValidationService {

    String nameTypeValidation(String fieldData, int maxAllowedCharactersPerWord, int nameMaxAllowedRows);

    StringBuilder priceTypeValidation(String fieldData);

    StringBuilder integerTypeValidation(String fieldData, int maxAllowedNumbers);

    <S> StringBuilder categoryPresenceValidation(ObservableList<S> listItem);

    StringBuilder categoryNameMatchValidation(String fieldData, Long currentCategoryId);

    StringBuilder barTableNameMatchValidation(int fieldData, Long currentBarTableId);

    StringBuilder productNameMatchValidation(String fieldData, Long currentProductId);

    StringBuilder barcodeMatchValidation(String fieldData, Long currentProductId);

    StringBuilder userNameMatchValidation(String fieldData, Long currentUserId);

    boolean validationErrorAlertBox(String errorMessage, Stage stage);
}
