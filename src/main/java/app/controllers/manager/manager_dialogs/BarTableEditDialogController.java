package app.controllers.manager.manager_dialogs;

import app.controllers.manager.manager_elements.BaseManageController;
import app.dtos.OrderDto;
import app.entities.BarTable;
import app.entities.User;
import app.services.api.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class BarTableEditDialogController implements ManagerDialogController {

    private static final String[] AVAILABLE_STATUS = {"active", "inactive"};
    private static final int TABLE_MAX_ALLOWED_NUMBERS = 3;

    @FXML private Label titleLabel;
    @FXML private TextField nameField;
    @FXML private TextFlow nameTextFlow;
    @FXML private Label nameFieldError;
    @FXML private ComboBox<String> statusComboBox;

    private BarTable barTable;
    private Stage stage;
    private TableView table;
    private BarTableService barTableService;
    private FieldValidationService fieldValidationService;
    private OrderService orderService;
    private int selectedIndex;
    private boolean isValid;

    @Autowired
    public BarTableEditDialogController(BarTableService barTableService, FieldValidationService fieldValidationService, OrderService orderService) {
        this.barTableService = barTableService;
        this.fieldValidationService = fieldValidationService;
        this.orderService = orderService;
    }

    @Override
    public void initialize() {
        this.isValid = true;
        nameValidationListener();
        this.hideErrorTextFlowContainer(this.nameTextFlow);
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
                    nameField.setText(String.valueOf(this.barTable.getNumber()));
                    addTableStatusChoices();
                    break;
                default:
                    titleLabel.setText("Add");
                    List<BarTable> allTables = this.barTableService.getAllBarTables();

                    Integer lastTableNumber =  allTables.size() >0 ? allTables.get(allTables.size()-1).getNumber() : 0;
                    nameField.setText(String.valueOf(lastTableNumber + 1));
                    addTableStatusChoices();
                    break;
            }

    }

    @Override
    public boolean isInputValid() {

        if (this.isValid){
            if (isTableModifiable()){
                return true;
            } else {
                this.fieldValidationService.validationErrorAlertBox("Cannot modify table with open order!", stage);
                stage.close();
                return false;
            }
        }
        return false;
//        if (isTableModifiable()){
//            return this.isValid;
//        } else {
//            this.fieldValidationService.validationErrorAlertBox("Cannot modify table with open order!", stage);
//            stage.close();
//            return false;
//        }
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
                this.barTable.setNumber(Integer.parseInt(nameField.getText()));
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
        if (isTableModifiable()) {
            this.barTable.setAvailable(false);
            this.barTableService.save(this.barTable);
        } else {
            this.fieldValidationService.validationErrorAlertBox("Cannot modify table with open order!", stage);
        }
    }

    //check for openOrders
    private boolean isTableModifiable(){
        if (null != this.barTable) {
            Long tableId = this.barTable.getId();
            OrderDto tableOpenOrder = this.orderService.findOpenOrderByTable(tableId);
            return null == tableOpenOrder;
        }
        return true;
    }

    private void addTableStatusChoices() {
        statusComboBox.getItems().addAll(AVAILABLE_STATUS);
        if (null != this.barTable){
            statusComboBox.getSelectionModel().select(this.barTable.getAvailable() ? "active" : "inactive");
        } else {
            statusComboBox.getSelectionModel().selectFirst();
        }
    }

    private void nameValidationListener(){
        if (null != this.nameField) {
            this.nameField.setOnKeyTyped(event -> {
                errorFieldDefault(this.nameTextFlow, this.nameFieldError);
            });
            this.nameField.focusedProperty().addListener((arg0, oldValue, newValue) -> {
                if (!newValue) {
                    StringBuilder errorMessage = new StringBuilder();

                    errorMessage.append(fieldValidationService.integerTypeValidation(nameField.getText(), TABLE_MAX_ALLOWED_NUMBERS));

                    Long currentBarTableId = null == this.barTable ? 0 : this.barTable.getId();
                    if (errorMessage.length() <= 0){
                        errorMessage.append(this.fieldValidationService.barTableNameMatchValidation(Integer.parseInt(this.nameField.getText()), currentBarTableId));
                    }

                    errorResultHandler(errorMessage, this.nameField, this.nameTextFlow, this.nameFieldError);
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
