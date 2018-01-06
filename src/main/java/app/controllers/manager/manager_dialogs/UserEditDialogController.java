package app.controllers.manager.manager_dialogs;

import app.entities.Role;
import app.entities.User;
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

    @Autowired
    public UserEditDialogController(UserService userService, PassKeyVerificationService passKeyVerificationService) {
        this.userService = userService;
        this.passKeyVerificationService = passKeyVerificationService;
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
            String roles = this.user.getRoles().stream().map(Role::getRole).collect(Collectors.joining(", "));
            rolesField.setText(roles);
        } else {
            titleLabel.setText("Add");
        }
    }

    @FXML
    private void handleOk() {
        //if (isInputValid()) {

            if (null == this.user){
                this.user = new User();
            }

            this.user.setName(nameField.getText());
            String password = passwordField.getText();
            if(!password.startsWith("$2a$")){
                this.user.setPasswordHash(passKeyVerificationService.hashPassKey(password));
            }
            this.user.setRoles(this.makeUserRolesSet());

            if (titleLabel.getText().equals("Add")){
                this.table.getItems().add(0, user);
            }

            this.table.refresh();
            stage.close();
 //       }
    }

    private Set<Role> makeUserRolesSet(){
        Set<Role> userRoles = this.user.getRoles();
        List<String> listRoles = Arrays.asList(rolesField.getText().split("\\s*,\\s*"));
        for (String role:listRoles) {
            boolean isRoleContained = false;
            for (Role userRole:userRoles) {
                if (userRole.getRole().equalsIgnoreCase(role)){
                    isRoleContained = true;
                    break;
                }
            }
            if (!isRoleContained){
                Role newRole = new Role();
                newRole.setRole(role);
                userRoles.add(newRole);
            }
        }
        return userRoles;
    }

    //TODO common for all dialog controllers
    @FXML
    private void handleCancel() {
        stage.close();
    }

////TODO make validation service
//    private boolean isInputValid() {
//
//        StringBuilder errorMessage = new StringBuilder();
//
//        //valid names must contains only letters, numbers, one or zero space and one or zero hyphen
//        if (nameField.getText() == null || nameField.getText().length() == 0) {
//            errorMessage.append("Name must not be empty!\r\n");
//        }
//        if (!nameField.getText().matches("^[A-Za-z0-9]+[ -]?[A-Za-z0-9]*$")){
//            errorMessage.append("Name must contain only letters, digits, zero or one space or hyphen!\r\n");
//        }
//
//        //validate price
//        if (priceField.getText() == null || priceField.getText().length() == 0) {
//            errorMessage.append("Price must contain at least one digit!\r\n");
//        }
//        if(priceField.getText().length() > MAX_ALLOWED_DIGITS_FOR_PRICE){
//            errorMessage.append(String.format("Price must be less than %s digits\r\n",MAX_ALLOWED_DIGITS_FOR_PRICE));
//        } else   {
//            try {
//                Double.parseDouble(priceField.getText());
//                BigDecimal priceBigDecimal = new BigDecimal("0.00000000000000000001");
//                if((priceBigDecimal.compareTo(BigDecimal.ZERO) == 0)){
//                    errorMessage.append("Price must not be 0\r\n");
//                }
//            } catch(Exception e) {
//                errorMessage.append("Price must contain only digits separated by dot e.g 1.02\r\n");
//            }
//        }
//
//        //validate image path
//        if (null != this.sourceFile){
//            if( !imagePathLabel.getText().isEmpty() && !imagePathLabel.getText().matches(IMG_PATH_PATTERN)) {
//                errorMessage.append("Image path is incorrect\r\n");
//            }
//        }
//        //validate barcode
//        if (barcodeField.getText() == null || barcodeField.getText().length() == 0){
//            errorMessage.append("Barcode must not be empty!\r\n");
//        }
//        if (!barcodeField.getText().matches("\\d+")){
//            errorMessage.append("Barcode must contains only digits!\r\n");
//        }
//        if (barcodeField.getText().length() > BARCODE_MAX_ALLOWED_NUMBERS){
//            errorMessage.append(String.format("Barcode must have less than %s!\r\n", BARCODE_MAX_ALLOWED_NUMBERS));
//        }
//
//        //check available
//        if (availableField.getText() == null || availableField.getText().length() == 0){
//            errorMessage.append("Available must not be empty!\r\n");
//        }
//
//        if (!availableField.getText().equalsIgnoreCase("NO") && !availableField.getText().equalsIgnoreCase("YES") ) {
//            errorMessage.append("Available must be Yes or No!\r\n");
//        }
//
//        if (errorMessage.length() == 0) {
//            return true;
//        } else {
//            // Show the error message.
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.initOwner(this.stage);
//            alert.setTitle("Invalid Fields");
//            alert.setHeaderText("Please correct invalid fields");
//            alert.setContentText(errorMessage.toString());
//
//            alert.showAndWait();
//
//            return false;
//        }
//    }

}
