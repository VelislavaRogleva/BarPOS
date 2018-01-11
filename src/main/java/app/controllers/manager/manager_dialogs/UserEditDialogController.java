package app.controllers.manager.manager_dialogs;

import app.entities.Role;
import app.entities.User;
import app.services.api.FieldValidationService;
import app.services.api.PassKeyVerificationService;
import app.services.api.UserService;
import javafx.collections.FXCollections;
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

        if (null != passwordField && !passwordField.getText().startsWith("$2a$")){
            errorMessage.append(this.passKeyVerificationService.validatePassKey(passwordField.getText()));
        }

//        if (errorMessage.length() <=0){
//            List<User> allUsers = this.userService.getAllRegisteredUsers();
//            for (User user:allUsers) {
//                if ((user.getName().equalsIgnoreCase(nameField.getText()) && this.stage.getTitle().equalsIgnoreCase("Add") ) ||
//                        (user.getName().equalsIgnoreCase(nameField.getText()) && ( (this.user.getId() > user.getId()) || (this.user.getId() < user.getId() ) ) ) ){
//                    errorMessage.append("The user exists. No override allowed!");
//                    break;
//                }
//            }
//        }

        return this.fieldValidationService.validationErrorAlertBox(errorMessage.toString(), this.stage);
    }

    @FXML
    private void handleOk() {
        try {
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
                this.userService.save(this.user);

                if (titleLabel.getText().equals("Add")){
                    this.table.getItems().add(0, this.user);
                }
                stage.close();
                this.table.refresh();
              }
        } catch (RuntimeException re) {
            this.fieldValidationService.validationErrorAlertBox("The user exists. No override allowed!", stage);
            this.table.setItems(FXCollections.observableArrayList(this.userService.getAllRegisteredUsers()));
        } catch (Exception e){
            this.fieldValidationService.validationErrorAlertBox("Cannot complete action! Incorrect field value", stage);
            this.table.setItems(FXCollections.observableArrayList(this.userService.getAllRegisteredUsers()));
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }


    private <S> void removeObjectFromDB() {
        this.user.setActive(false);
        this.userService.save(this.user);
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
