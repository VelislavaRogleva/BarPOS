package app.controllers.manager.crud.dialogs;

import app.dtos.OrderDto;
import app.entities.BarTable;
import app.enums.ErrorMessages;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class BarTableEditDialogController implements ManagerDialogController {

    private static final String[] AVAILABLE_STATUS = {"active", "inactive"};
    private static final int TABLE_MAX_ALLOWED_NUMBERS = 3;
    private static final String TITLE_NAME_EDIT = "Edit";
    private static final String TITLE_NAME_DELETE = "Delete";
    private static final String TITLE_NAME_ADD = "Add";
    private static final int DEFAULT_TABLE_NUMBER = 0;
    private static final int NEXT_TABLE_NUMBER_OFFSET = 1;
    private static final int TABLE_VIEW_ADD_INDEX = 0;
    private static final int NULL_TABLE_ID = 0;
    private static final double HIDDEN_TEXT_FIELD_DEFAULT_HEIGHT = 0.0;

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
                case TITLE_NAME_DELETE:
                    break;
                case TITLE_NAME_EDIT:
                    titleLabel.setText(TITLE_NAME_EDIT);
                    nameField.setText(String.valueOf(this.barTable.getNumber()));
                    addTableStatusChoices();
                    break;
                default:
                    titleLabel.setText(TITLE_NAME_ADD);
                    List<BarTable> allTables = this.barTableService.getAllBarTables();

                    Integer lastTableNumber =  allTables.size() > 0 ? allTables.get(allTables.size()-1).getNumber() : DEFAULT_TABLE_NUMBER;
                    nameField.setText(String.valueOf(lastTableNumber + NEXT_TABLE_NUMBER_OFFSET));
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
                this.fieldValidationService.validationErrorAlertBox(ErrorMessages.NO_TABLE_MODIFICATION_ERROR.getMessage(), stage);
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
            if (this.stage.getTitle().equalsIgnoreCase(TITLE_NAME_DELETE)){
                removeObjectFromDB();
                stage.close();

            } else if(isInputValid()) {

                if (null == this.barTable){
                    this.barTable = new BarTable();
                }
                this.barTable.setNumber(Integer.parseInt(nameField.getText()));
                this.barTable.setAvailable(statusComboBox.getValue().equalsIgnoreCase(AVAILABLE_STATUS[0]));

                this.barTableService.addNewTable(this.barTable);

                if (titleLabel.getText().equals(TITLE_NAME_ADD)){
                    this.table.getItems().add(TABLE_VIEW_ADD_INDEX, barTable);
                }
                stage.close();
            }
        } catch (RuntimeException re) {
            this.fieldValidationService.validationErrorAlertBox(ErrorMessages.NO_OVERRIDE_TABLE.getMessage(), stage);
            this.table.setItems(FXCollections.observableArrayList(this.barTableService.getAllBarTables()));
        }catch (Exception e){
            this.fieldValidationService.validationErrorAlertBox(ErrorMessages.BAD_ACTION.getMessage(), stage);
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
            this.fieldValidationService.validationErrorAlertBox(ErrorMessages.NO_TABLE_MODIFICATION_ERROR.getMessage(), stage);
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
            statusComboBox.getSelectionModel().select(this.barTable.getAvailable() ? AVAILABLE_STATUS[0] : AVAILABLE_STATUS[1]);
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

                    Long currentBarTableId = null == this.barTable ? NULL_TABLE_ID : this.barTable.getId();
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
