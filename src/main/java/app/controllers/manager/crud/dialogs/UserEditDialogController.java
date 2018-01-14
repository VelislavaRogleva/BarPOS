package app.controllers.manager.crud.dialogs;

import app.entities.User;
import app.enums.ErrorMessages;
import app.services.api.FieldValidationService;
import app.services.api.PassKeyVerificationService;
import app.services.api.UserService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UserEditDialogController implements ManagerDialogController {

    private static final String[] AVAILABLE_STATUS = {"active", "inactive"};
    private static final String[] AVAILABLE_ROLES = {"WAITER", "ADMIN"};
    private static final String TITLE_NAME_DELETE = "Delete";
    private static final String TITLE_NAME_EDIT = "Edit";
    private static final String TITLE_NAME_ADD = "Add";
    private static final String HASHED_PASSKEY_START_STRING = "$2a$";
    private static final int TABLE_VIEW_ADD_INDEX = 0;
    private static final int MAX_ALLOWED_CHARACTERS_PER_WORD = 20;
    private static final int NAME_MAX_ALLOWED_ROWS = 1;

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextFlow nameTextFlow;
    @FXML private Label nameFieldError;
    @FXML private TextField passkeyField;
    @FXML private TextFlow passkeyTextFlow;
    @FXML private Label passkeyFieldError;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private ComboBox<String> roleComboBox;

    private User user;
    private Stage stage;
    private TableView table;
    private UserService userService;
    private PassKeyVerificationService passKeyVerificationService;
    private FieldValidationService fieldValidationService;
    private int selectedIndex;
    private boolean isValid;

    @Autowired
    public UserEditDialogController(UserService userService, PassKeyVerificationService passKeyVerificationService, FieldValidationService fieldValidationService) {
        this.userService = userService;
        this.passKeyVerificationService = passKeyVerificationService;
        this.fieldValidationService = fieldValidationService;
    }

    @Override
    public void initialize() {
        isValid = true;
        this.hideErrorTextFlowContainer(this.nameTextFlow);
        this.hideErrorTextFlowContainer(this.passkeyTextFlow);
        this.nameValidationListener();
        this.passkeyValidationListener();

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
    public <S> void setEditObject(S user) {
        this.user = (User)user;
        titleLabel.setText(this.stage.getTitle());

        switch(this.stage.getTitle()){
            case TITLE_NAME_DELETE:
                break;
            case TITLE_NAME_EDIT:
                titleLabel.setText(TITLE_NAME_DELETE);
                nameField.setText(this.user.getName());
                passkeyField.setText(this.user.getPasswordHash());
                addUserRoleChoices();
                addUserStatusChoices();
                break;
            default:
                titleLabel.setText(TITLE_NAME_ADD);
                addUserRoleChoices();
                addUserStatusChoices();
                break;
        }
    }

    @Override
    public boolean isInputValid() {
        return this.isValid;
    }

    @FXML
    private void handleOk() {
        try {
            if (this.stage.getTitle().equalsIgnoreCase(TITLE_NAME_DELETE)){
                removeObjectFromDB();
                stage.close();

            } else if(isInputValid()) {

                if (null == this.user){
                    this.user = new User();
                }

                this.user.setName(nameField.getText());
                String password = passkeyField.getText();
                if(!password.startsWith(HASHED_PASSKEY_START_STRING)){
                    this.user.setPasswordHash(passKeyVerificationService.hashPassKey(password));
                }
                this.user.setRole(roleComboBox.getValue());
                this.user.setActive(statusComboBox.getValue().equalsIgnoreCase(AVAILABLE_STATUS[0]));
                this.userService.save(this.user);

                if (titleLabel.getText().equals(TITLE_NAME_ADD)){
                    this.table.getItems().add(TABLE_VIEW_ADD_INDEX, this.user);
                }
                stage.close();
                this.table.refresh();
              }
        } catch (RuntimeException re) {
            this.fieldValidationService.validationErrorAlertBox(ErrorMessages.USER_NO_OVERRIDE.getMessage(), stage);
            this.table.setItems(FXCollections.observableArrayList(this.userService.getAllRegisteredUsers()));
        } catch (Exception e){
            this.fieldValidationService.validationErrorAlertBox(ErrorMessages.BAD_ACTION.getMessage(), stage);
            this.table.setItems(FXCollections.observableArrayList(this.userService.getAllRegisteredUsers()));
        }
    }

    @FXML
    private void handleCancel() {
        this.table.refresh();
        stage.close();
    }

    private <S> void removeObjectFromDB() {
        this.user.setActive(false);
        this.userService.save(this.user);
    }


    private void addUserStatusChoices() {
        statusComboBox.getItems().addAll(AVAILABLE_STATUS);
        if (null != this.user){
            this.statusComboBox.getSelectionModel().select(this.user.getActive() ? AVAILABLE_STATUS[0] : AVAILABLE_STATUS[1]);
        } else {
            this.statusComboBox.getSelectionModel().selectFirst();
        }
    }

    private void addUserRoleChoices() {
        this.roleComboBox.getItems().addAll(AVAILABLE_ROLES);
        if (null != this.user){
            this.roleComboBox.getSelectionModel().select(this.user.getRole());
        } else {
            this.roleComboBox.getSelectionModel().selectFirst();
        }
    }

    private void nameValidationListener(){
        if (null != this.nameField) {
            this.nameField.setOnKeyTyped(event -> {
                errorFieldDefault(nameTextFlow, nameFieldError);
            });

            this.nameField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
                if (!newValue && isValid) {
                    StringBuilder errorMessage = new StringBuilder();

                    errorMessage.append(this.fieldValidationService.nameTypeValidation(this.nameField.getText(), MAX_ALLOWED_CHARACTERS_PER_WORD, NAME_MAX_ALLOWED_ROWS));

                    //check if name exist
                    Long currentUserId = null == this.user ? 0 : this.user.getId();
                    if (errorMessage.length() <= 0){
                        errorMessage.append(this.fieldValidationService.userNameMatchValidation(this.nameField.getText(), currentUserId));
                    }

                    this.errorResultHandler(errorMessage, this.nameField, this.nameTextFlow, this.nameFieldError);
                }
            });
        }
    }

    private void passkeyValidationListener(){
        if (null != this.passkeyField) {
            this.passkeyField.setOnKeyTyped(event -> {
                errorFieldDefault(passkeyTextFlow, passkeyFieldError);
            });

            this.passkeyField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
                if (!newValue && isValid) {
                    StringBuilder errorMessage = new StringBuilder();

                    if (null != passkeyField && !passkeyField.getText().startsWith(HASHED_PASSKEY_START_STRING)){
                        errorMessage.append(this.passKeyVerificationService.validatePassKey(passkeyField.getText()));
                    }

                    this.errorResultHandler(errorMessage, this.passkeyField, this.passkeyTextFlow, this.passkeyFieldError);
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
