package app.controllers.manager.crud.dialogs;

import app.entities.Category;
import app.enums.ErrorMessages;
import app.services.api.CategoryService;
import app.services.api.FieldValidationService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CategoryEditDialogController implements ManagerDialogController {

    private static final String TITLE_NAME_DELETE = "Delete";
    private static final String TITLE_NAME_ADD = "Add";
    private static final String TITLE_NAME_EDIT = "Edit";
    private static final int TABLE_VIEW_ADD_INDEX = 0;
    private static final int MAX_ALLOWED_CHARACTERS_PER_WORD = 9;
    private static final int NAME_MAX_ALLOWED_ROWS = 3;
    private static final int NULL_CATEGORY_ID = 0;
    private static final double HIDDEN_TEXT_FIELD_DEFAULT_HEIGHT = 0.0;

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextFlow nameTextFlow;
    @FXML private Label nameFieldError;

    private Category category;
    private Stage stage;
    private TableView table;
    private CategoryService categoryService;
    private FieldValidationService fieldValidationService;
    private int selectedIndex;
    private boolean isValid;

    @Autowired
    public CategoryEditDialogController(CategoryService categoryService, FieldValidationService fieldValidationService) {
        this.categoryService = categoryService;
        this.fieldValidationService = fieldValidationService;
    }

    @Override
    public void initialize() {
        this.isValid = true;
        this.hideErrorTextFlowContainer(this.nameTextFlow);
        this.nameValidationListener();
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
    public <S> void setEditObject(S category) {
        this.category = (Category) category;
        titleLabel.setText(this.stage.getTitle());

        switch(this.stage.getTitle()){
            case TITLE_NAME_DELETE:
                break;
            case TITLE_NAME_EDIT:
                nameField.setText(this.category.getName());
                break;
            default:
                break;
        }
    }

    @Override
    public void setSelectedIndex(int index){
        this.selectedIndex = index;
    }

    @Override
    public boolean isInputValid() {
        return this.isValid;
    }

    @FXML
    private void handleOk() {
        try{
            if (this.stage.getTitle().equalsIgnoreCase(TITLE_NAME_DELETE)){
                removeObjectFromDB();
                stage.close();
            } else if (this.isInputValid()) {
                if (null == this.category){
                    this.category = new Category();
                }
                this.category.setName(nameField.getText());

                this.categoryService.save(this.category);

                if (titleLabel.getText().equals(TITLE_NAME_ADD)){
                    this.table.getItems().add(TABLE_VIEW_ADD_INDEX, category);
                }
                stage.close();
            }
        } catch (RuntimeException re) {
            this.fieldValidationService.validationErrorAlertBox(ErrorMessages.CATEGORY_NAME_TAKEN.getMessage(), stage);
            this.table.setItems(FXCollections.observableArrayList(this.categoryService.getAllCategories()));
        } catch (Exception e){
            this.fieldValidationService.validationErrorAlertBox(ErrorMessages.BAD_ACTION.getMessage(), stage);
            this.table.setItems(FXCollections.observableArrayList(this.categoryService.getAllCategories()));
        }
    }

    @FXML
    private void handleCancel() {
        if (null != this.nameField){
            this.nameField.setOnKeyTyped(event -> nameFieldError.setText(""));
            this.nameField.setOnMouseClicked(event -> nameFieldError.setText(""));
        }
        stage.close();
    }

    private void nameValidationListener(){
        if (null != this.nameField) {
            this.nameField.setOnKeyTyped(event -> errorFieldDefault(this.nameTextFlow, this.nameFieldError));
            this.nameField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
                if (!newValue){
                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append(this.fieldValidationService.nameTypeValidation(this.nameField.getText(), MAX_ALLOWED_CHARACTERS_PER_WORD, NAME_MAX_ALLOWED_ROWS));
                    Long currentCategoryId = null == this.category ? NULL_CATEGORY_ID : this.category.getId();
                    errorMessage.append(this.fieldValidationService.categoryNameMatchValidation(nameField.getText(), currentCategoryId));

                    errorResultHandler(errorMessage, this.nameField, this.nameTextFlow, this.nameFieldError);
                }
            });
        }
    }

    private <S> void removeObjectFromDB() {
        try {
            this.categoryService.remove(category);
            this.table.getItems().remove(this.selectedIndex);
        } catch (RuntimeException re){
            this.fieldValidationService.validationErrorAlertBox(ErrorMessages.CATEGORY_NOT_EMPTY.getMessage(), stage);
            this.table.setItems(FXCollections.observableArrayList(this.categoryService.getAllCategories()));
        }
    }

    private void hideErrorTextFlowContainer(TextFlow textFlow){
        textFlow.setVisible(false);
        textFlow.setPrefHeight(HIDDEN_TEXT_FIELD_DEFAULT_HEIGHT);
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
