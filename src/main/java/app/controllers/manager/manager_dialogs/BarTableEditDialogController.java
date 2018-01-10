package app.controllers.manager.manager_dialogs;

import app.controllers.manager.manager_elements.BaseManageController;
import app.entities.BarTable;
import app.entities.User;
import app.services.api.BarTableService;
import app.services.api.FieldValidationService;
import app.services.api.PassKeyVerificationService;
import app.services.api.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class BarTableEditDialogController implements ManagerDialogController {

    private static final String[] AVAILABLE_STATUS = {"active", "inactive"};
    private static final int TABLE_MAX_ALLOWED_NUMBERS = 1000;

    @FXML private Label titleLabel;
    @FXML private Label tableId;
    @FXML private ComboBox<String> statusComboBox;

    private BarTable barTable;
    private Stage stage;
    private TableView table;
    private BarTableService barTableService;
    private FieldValidationService fieldValidationService;
    private int selectedIndex;

    @Autowired
    public BarTableEditDialogController(BarTableService barTableService, FieldValidationService fieldValidationService) {
        this.barTableService = barTableService;
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
    public <S> void setEditObject(S table) {
        this.barTable = (BarTable)table;
        titleLabel.setText(this.stage.getTitle());

        switch(this.stage.getTitle()){
            case "Delete":
                break;
            case "Edit":
                titleLabel.setText("Edit");
                tableId.setText(String.valueOf(this.barTable.getId()));
                addTableStatusChoices();
                break;
            default:
                titleLabel.setText("Add");
                //List<BarTable> allTables = this.barTableService.getAllBarTables();
                tableId.setText("auto generate");
                addTableStatusChoices();
                break;
        }
    }

    @Override
    public boolean isInputValid() {

        StringBuilder errorMessage = new StringBuilder();

        errorMessage.append(this.fieldValidationService.booleanTypeValidation(statusComboBox.getValue(), statusComboBox.getPromptText(), AVAILABLE_STATUS[0], AVAILABLE_STATUS[1]));

        return this.fieldValidationService.validationErrorAlertBox(errorMessage.toString(), this.stage);
    }

    @FXML
    private void handleOk() {
        if (this.stage.getTitle().equalsIgnoreCase("Delete")){
            removeObjectFromDB();
            stage.close();

        } else if(isInputValid()) {

            if (null == this.barTable){
                this.barTable = new BarTable();
            }
            this.barTable.setAvailable(statusComboBox.getValue().equalsIgnoreCase("active"));

            this.barTableService.addNewTable(this.barTable);

            if (titleLabel.getText().equals("Add")){
                this.table.getItems().add(0, barTable);
            }
            stage.close();
          }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }


    private <S> void removeObjectFromDB() {
        this.barTable.setAvailable(false);
    }


    private void addTableStatusChoices() {
        statusComboBox.getItems().addAll(AVAILABLE_STATUS);
        if (null != this.barTable){
            statusComboBox.getSelectionModel().select(this.barTable.getAvailable() ? "active" : "inactive");
        } else {
            statusComboBox.getSelectionModel().selectFirst();
        }
    }
}
