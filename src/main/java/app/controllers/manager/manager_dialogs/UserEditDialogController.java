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

    private static final int MAX_ALLOWED_DIGITS_FOR_PRICE = 9;
    private static final int BARCODE_MAX_ALLOWED_NUMBERS = 15;
    private static final String IMG_PATH_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
    private static final String SYSTEM_DIR = "user.dir";


    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextField passwordField;
    @FXML private TextField rolesField;

    private User user;
    private Stage stage;
    private TableView table;
    private UserService userService;
    private PassKeyVerificationService passKeyVerificationService;
    private FieldValidationService fieldValidationService;

    @Autowired
    public UserEditDialogController(UserService userService, PassKeyVerificationService passKeyVerificationService, FieldValidationService fieldValidationService) {
        this.userService = userService;
        this.passKeyVerificationService = passKeyVerificationService;
        this.fieldValidationService = fieldValidationService;
    }

    @Override
    public void initialize() {
//        this.sourceFile = new File("");
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
    public <S> void setEditObject(S user) {
        this.user = (User)user;

        if (user != null){
            titleLabel.setText("Edit");
            nameField.setText(this.user.getName());
            passwordField.setText(this.user.getPasswordHash());
            String roles = this.user.getRole().getRole();
            rolesField.setText(roles);
        } else {
            titleLabel.setText("Add");
        }
    }

    @Override
    public boolean isInputValid() {

        StringBuilder errorMessage = new StringBuilder();

        errorMessage.append(this.fieldValidationService.nameValidation(nameField.getText()));
        //TODO
//        errorMessage.append(this.fieldValidationService.passwordValidation(passwordField.getText()));
        //TODO
        //       errorMessage.append(this.fieldValidationService.roleValidation(rolesField.getText()));

        return this.fieldValidationService.validationErrorAlertBox(errorMessage, this.stage);
    }

    //TODO
    @Override
    public void removeObjectFromDB(Object object) {
        User user = (User) object;
 //       this.userService.removeUser(user);
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {

            if (null == this.user){
                this.user = new User();
                this.user.setRole(new Role());
                this.user.getRole().setRole(rolesField.getText());
            }

            this.user.setName(nameField.getText());
            String password = passwordField.getText();
            if(!password.startsWith("$2a$")){
                this.user.setPasswordHash(passKeyVerificationService.hashPassKey(password));
            }

            if (!this.user.getRole().getRole().equalsIgnoreCase(rolesField.getText())){
                this.user.getRole().setRole(rolesField.getText());
            }


            if (titleLabel.getText().equals("Add")){
                this.table.getItems().add(0, user);
            }

            this.userService.save(this.user);

            this.table.refresh();
            stage.close();
          }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }


}
