package app.controllers.manager.manager_elements;

import app.controllers.manager.crud_buttons.DeleteButtonCell;
import app.controllers.manager.crud_buttons.EditButtonCell;
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
        this.genericTable.getStyleClass().addAll("contentTable");
        this.genericTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        double columnWidth = super.calculateColumnWidth(OBJECT_COUNT_PROPERTIES);

        // create table columns
        //edit button column
        TableColumn<Product, Boolean> editButtonColumn = new TableColumn<>();
        setButtonColumnProperties(editButtonColumn, "editColumn");
        editButtonColumn.setCellFactory(new Callback<TableColumn<Product, Boolean>, TableCell<Product, Boolean>>() {
            @Override
            public TableCell<Product, Boolean> call(TableColumn<Product, Boolean> param) {
                EditButtonCell editButton = new EditButtonCell();
                editButton.createButton(genericTable, ManageBarTableController.super.getStageManager());
                return editButton;
            }
        });

        //number column
        TableColumn<BarTable, Integer> nameColumn = new TableColumn<>("name");
        setColumnProperties(nameColumn, columnWidth);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("number"));

        //available column
        TableColumn<BarTable, Boolean> availableColumn = new TableColumn<>("available");
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
                        setText("YES");
                        setTextFill(Color.color(0.54, 0.67, 0.09));
                    } else{
                        setText("NO");
                        setTextFill(Color.color(0.69, 0.047, 0.18) );
                    }

                }
            }
        });
        availableColumn.setCellValueFactory(new PropertyValueFactory<BarTable, Boolean>("available"));

        //delete button column
        TableColumn<Product, Boolean> deleteButtonColumn = new TableColumn<>();
        setButtonColumnProperties(deleteButtonColumn, "deleteColumn");
        deleteButtonColumn.setCellFactory(new Callback<TableColumn<Product, Boolean>, TableCell<Product, Boolean>>() {
            @Override
            public TableCell<Product, Boolean> call(TableColumn<Product, Boolean> param) {
//                DeleteButtonCell deleteButton = new DeleteButtonCell(ManageBarTableController.super.getStageManager());
//                deleteButton.createButton(genericTable);

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
