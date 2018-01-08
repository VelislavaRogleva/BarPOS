package app.controllers.manager.manager_dialogs;

import app.entities.Role;
import app.entities.User;
import app.services.api.FieldValidationService;
import app.services.api.PassKeyVerificationService;
import app.services.api.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class UserEditDialogController implements ManagerDialogController {

    private static final String[] AVAILABLE_STATUS = {"active", "inactive"};

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField passwordField;
    @FXML private TextField rolesField;
    @FXML private ComboBox<String> statusComboBox;

    private User user;
    private Stage stage;
    private TableView table;
    private UserService userService;
    private PassKeyVerificationService passKeyVerificationService;
    private FieldValidationService fieldValidationService;
    private int selectedIndex;

    @Autowired
    public UserEditDialogController(UserService userService, PassKeyVerificationService passKeyVerificationService, FieldValidationService fieldValidationService) {
        this.userService = userService;
        this.passKeyVerificationService = passKeyVerificationService;
        this.fieldValidationService = fieldValidationService;
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
    public void setSelectedIndex(int index){
        this.selectedIndex = index;
    }

    @Override
    public <S> void setEditObject(S user) {
        this.user = (User)user;
        titleLabel.setText(this.stage.getTitle());

        switch(this.stage.getTitle()){
            case "Delete":
                break;
            case "Edit":
                titleLabel.setText("Edit");
                nameField.setText(this.user.getName());
                passwordField.setText(this.user.getPasswordHash());
                rolesField.setText(this.user.getRole());
                addUserStatusChoices();
                break;
            default:
                titleLabel.setText("Add");
                addUserStatusChoices();
                break;
        }
    }

    @Override
    public boolean isInputValid() {

        StringBuilder errorMessage = new StringBuilder();

        errorMessage.append(this.fieldValidationService.nameTypeValidation(nameField.getText(), nameField.getPromptText()));
        errorMessage.append(this.fieldValidationService.nameTypeValidation(rolesField.getText(), rolesField.getPromptText()));
        errorMessage.append(this.fieldValidationService.booleanTypeValidation(statusComboBox.getValue(), statusComboBox.getPromptText(), AVAILABLE_STATUS[0], AVAILABLE_STATUS[1]));
        if (null != this.user && !passwordField.getText().equals(this.user.getPasswordHash())){
            errorMessage.append(this.passKeyVerificationService.validatePassKey(passwordField.getText()));
        }

        return this.fieldValidationService.validationErrorAlertBox(errorMessage.toString(), this.stage);
    }

    @FXML
    private void handleOk() {
        if (this.stage.getTitle().equalsIgnoreCase("Delete")){
            removeObjectFromDB();
            stage.close();

        } else if(isInputValid()) {

            if (null == this.user){
                this.user = new User();
            }

            this.user.setName(nameField.getText());
            String password = passwordField.getText();
            if(!password.startsWith("$2a$")){
                this.user.setPasswordHash(passKeyVerificationService.hashPassKey(password));
            }
            this.user.setRole(rolesField.getText());
            this.user.setActive(statusComboBox.getValue().equalsIgnoreCase("active"));

            if (titleLabel.getText().equals("Add")){
                this.table.getItems().add(0, user);
            }

            this.userService.save(this.user);
            stage.close();
          }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }


    private <S> void removeObjectFromDB() {
        this.user.setActive(false);
    }


    private void addUserStatusChoices() {
        statusComboBox.getItems().addAll(AVAILABLE_STATUS);
        if (null != this.user){
            statusComboBox.getSelectionModel().select(this.user.getActive() ? "active" : "inactive");
        } else {
            statusComboBox.getSelectionModel().selectFirst();
        }
    }
}
