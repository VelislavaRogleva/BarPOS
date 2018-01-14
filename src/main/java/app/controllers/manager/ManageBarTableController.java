package app.controllers.manager;

import app.controllers.manager.crud.buttons.DeleteButtonCell;
import app.controllers.manager.crud.buttons.EditButtonCell;
import app.cores.StageManager;
import app.entities.BarTable;
import app.entities.Product;
import app.services.api.BarTableService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ManageBarTableController extends BaseManageController {

    private static final int OBJECT_COUNT_PROPERTIES = 2;
    private static final String GENERIC_TABLE_STYLE_CLASS_NAME = "contentTable";
    private static final String EDIT_COLUMN_TITLE = "editColumn";
    private static final String NAME_COLUMN_TITLE = "name";
    private static final String AVAILABLE_COLUMN_TITLE = "available";
    private static final String NUMBER_COLUMN_VALUE_FIELD_NAME = "number";
    private static final String STATUS_YES = "YES";
    private static final String STATUS_NO = "NO";
    private static final double STATUS_YES_RED_COLOUR = 0.54;
    private static final double STATUS_YES_GREEN_COLOUR = 0.67;
    private static final double STATUS_YES_BLUE_COLOUR = 0.09;
    private static final double STATUS_NO_RED_COLOUR = 0.69;
    private static final double STATUS_NO_GREEN_COLOUR = 0.047;
    private static final double STATUS_NO_BLUE_COLOUR = 0.18;
    private static final String AVAILABLE_COLUMN_VALUE_FIELD_NAME = "available";
    private static final String DELETE_COLUMN_TITLE = "deleteColumn";

    private BarTableService barTableService;
    private TableView genericTable;

    @Autowired
    @Lazy
    public ManageBarTableController(StageManager stageManager, BarTableService barTableService) {
        super(stageManager);
        this.barTableService = barTableService;
    }

    @Override
    public void initialize() {
        this.createTable();
        this.getDbData();
        super.addButtonAction(this.genericTable);
    }

    @Override
    void createTable() {

        this.genericTable = new TableView();
        this.genericTable.getStyleClass().addAll(GENERIC_TABLE_STYLE_CLASS_NAME);
        this.genericTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        double columnWidth = super.calculateColumnWidth(OBJECT_COUNT_PROPERTIES);

        // create table columns
        //edit button column
        TableColumn<Product, Boolean> editButtonColumn = new TableColumn<>();
        setButtonColumnProperties(editButtonColumn, EDIT_COLUMN_TITLE);
        editButtonColumn.setCellFactory(param -> {
            EditButtonCell editButton = new EditButtonCell();
            editButton.createButton(genericTable, ManageBarTableController.super.getStageManager());
            return editButton;
        });

        //number column
        TableColumn<BarTable, Integer> nameColumn = new TableColumn<>(NAME_COLUMN_TITLE);
        setColumnProperties(nameColumn, columnWidth);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>(NUMBER_COLUMN_VALUE_FIELD_NAME));

        //available column
        TableColumn<BarTable, Boolean> availableColumn = new TableColumn<>(AVAILABLE_COLUMN_TITLE);
        setColumnProperties(availableColumn, columnWidth);
        //change true to YES and false to NO
        availableColumn.setCellFactory(ac-> new TableCell<BarTable, Boolean>(){
            @Override
            protected  void updateItem(Boolean item, boolean empty){
                super.updateItem(item, empty);
                if (empty){
                    setText("");
                } else {
                    if(item){
                        setText(STATUS_YES);
                        setTextFill(Color.color(STATUS_YES_RED_COLOUR, STATUS_YES_GREEN_COLOUR, STATUS_YES_BLUE_COLOUR));
                    } else{
                        setText(STATUS_NO);
                        setTextFill(Color.color(STATUS_NO_RED_COLOUR, STATUS_NO_GREEN_COLOUR, STATUS_NO_BLUE_COLOUR) );
                    }
                }
            }
        });
        availableColumn.setCellValueFactory(new PropertyValueFactory<BarTable, Boolean>(AVAILABLE_COLUMN_VALUE_FIELD_NAME));

        //delete button column
        TableColumn<Product, Boolean> deleteButtonColumn = new TableColumn<>();
        setButtonColumnProperties(deleteButtonColumn, DELETE_COLUMN_TITLE);
        deleteButtonColumn.setCellFactory(new Callback<TableColumn<Product, Boolean>, TableCell<Product, Boolean>>() {
            @Override
            public TableCell<Product, Boolean> call(TableColumn<Product, Boolean> param) {

                DeleteButtonCell deleteButton = new DeleteButtonCell();
                deleteButton.createButton(genericTable, ManageBarTableController.super.getStageManager());

                return deleteButton;
            }
        });

        //add columns to tableView
        this.genericTable.getColumns().addAll(editButtonColumn, nameColumn, availableColumn, deleteButtonColumn);
        super.getMainContentAnchor().getChildren().add(this.genericTable);
    }

    private void getDbData(){
        if (super.getStageManager().getSearchResults().size()!=0){
            this.genericTable.setItems(this.invokeBySearch(super.getStageManager().getSearchResults()));
            super.getStageManager().getSearchResults().clear();
        }  else {
            ObservableList<BarTable> availableTable =  FXCollections.observableArrayList(this.barTableService.getAllBarTables());
            this.genericTable.setItems(availableTable);
        }
    }

    private <S> ObservableList<BarTable> invokeBySearch(List<S> searchResults){
        List<BarTable> availableTable = (List<BarTable>) searchResults;
        return FXCollections.observableArrayList(availableTable);
    }

}
