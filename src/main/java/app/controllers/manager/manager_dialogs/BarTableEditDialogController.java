package app.controllers.manager.manager_dialogs;

import app.controllers.manager.manager_elements.BaseManageController;
import app.entities.BarTable;
import app.entities.User;
import app.services.api.BarTableService;
import app.services.api.FieldValidationService;
import app.services.api.PassKeyVerificationService;
import app.services.api.UserService;
import javafx.collections.FXCollections;
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
    @FXML private TextField tableNameField;
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
                tableNameField.setText(String.valueOf(this.barTable.getNumber()));
                addTableStatusChoices();
                break;
            default:
                titleLabel.setText("Add");
                List<BarTable> allTables = this.barTableService.getAllBarTables();

                Integer lastTableNumber =  allTables.size() >0 ? allTables.get(allTables.size()-1).getNumber() : 0;
                tableNameField.setText(String.valueOf(lastTableNumber + 1));
                addTableStatusChoices();
                break;
        }
    }

    @Override
    public boolean isInputValid() {

        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(fieldValidationService.integerTypeValidation(tableNameField.getText(), tableNameField.getPromptText(), TABLE_MAX_ALLOWED_NUMBERS));
        errorMessage.append(this.fieldValidationService.booleanTypeValidation(statusComboBox.getValue(), statusComboBox.getPromptText(), AVAILABLE_STATUS[0], AVAILABLE_STATUS[1]));

//        if (errorMessage.length() <=0 ){
//            List<BarTable> allBarTable = this.barTableService.getAllBarTables();
//            for (BarTable barTable:allBarTable) {
//                if( (barTable.getNumber() == Integer.parseInt(tableNameField.getText()) && this.stage.getTitle().equalsIgnoreCase("Add")) ||
//                        (barTable.getNumber() == Integer.parseInt(tableNameField.getText()) && ( (this.barTable.getId() > barTable.getId()) || (this.barTable.getId() < barTable.getId())  ) )){
//                    errorMessage.append("This table exists. No override allowed!");
//                    break;
//                }
//            }
//        }

        return this.fieldValidationService.validationErrorAlertBox(errorMessage.toString(), this.stage);
    }

    @FXML
    private void handleOk() {
        try{
            if (this.stage.getTitle().equalsIgnoreCase("Delete")){
                removeObjectFromDB();
                stage.close();

            } else if(isInputValid()) {

                if (null == this.barTable){
                    this.barTable = new BarTable();
                }
                this.barTable.setNumber(Integer.parseInt(tableNameField.getText()));
                this.barTable.setAvailable(statusComboBox.getValue().equalsIgnoreCase("active"));

                this.barTableService.addNewTable(this.barTable);

                if (titleLabel.getText().equals("Add")){
                    this.table.getItems().add(0, barTable);
                }
                stage.close();
            }
        } catch (RuntimeException re) {
            this.fieldValidationService.validationErrorAlertBox("This table exists. No override allowed!", stage);
            this.table.setItems(FXCollections.observableArrayList(this.barTableService.getAllBarTables()));
        }catch (Exception e){
            this.fieldValidationService.validationErrorAlertBox("Cannot complete action! Incorrect field value", stage);
            this.table.setItems(FXCollections.observableArrayList(this.barTableService.getAllBarTables()));
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
