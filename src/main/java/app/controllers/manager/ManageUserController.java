package app.controllers.manager;

import app.controllers.manager.crud.buttons.DeleteButtonCell;
import app.controllers.manager.crud.buttons.EditButtonCell;
import app.cores.StageManager;
import app.entities.Product;
import app.entities.User;
import app.services.api.UserService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ManageUserController extends BaseManageController {

    private static final int OBJECT_COUNT_PROPERTIES = 3;
    private static final String GENERIC_TABLE_STYLE_CLASS_NAME = "contentTable";
    private static final String EDIT_COLUMN_TITLE = "editColumn";
    private static final String NAME_COLUMN_TITLE = "name";
    private static final String NAME_COLUMN_VALUE_FIELD_NAME = "name";
    private static final String ROLE_COLUMN_TITLE = "role";
    private static final String ROLE_COLUMN_VALUE_FIELD_NAME = "role";
    private static final String STATUS_COLUMN_TITLE = "status";
    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_INACTIVE = "inactive";
    private static final double STATUS_ACTIVE_RED_COLOR = 0.54;
    private static final double STATUS_ACTIVE_GREEN_COLOR = 0.67;
    private static final double STATUS_ACTIVE_BLUE_COLOR = 0.09;
    private static final double STATUS_INACTIVE_RED_COLOR = 0.69;
    private static final double STATUS_INACTIVE_GREEN_COLOR = 0.047;
    private static final double STATUS_INACTIVE_BLUE_COLOR = 0.18;
    private static final String DELETE_COLUMN_TITLE = "deleteColumn";

    private UserService userService;
    private TableView genericTable;

    @Autowired
    @Lazy
    public ManageUserController(StageManager stageManager, UserService userService) {
        super(stageManager);
        this.userService = userService;
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
        editButtonColumn.setCellFactory(new Callback<TableColumn<Product, Boolean>, TableCell<Product, Boolean>>() {
            @Override
            public TableCell<Product, Boolean> call(TableColumn<Product, Boolean> param) {
                EditButtonCell editButton = new EditButtonCell();
                editButton.createButton(genericTable, ManageUserController.super.getStageManager());
                return editButton;
            }
        });

        //name column
        TableColumn<User, String> nameColumn = new TableColumn<>(NAME_COLUMN_TITLE);
        setColumnProperties(nameColumn, columnWidth);
        nameColumn.setCellFactory(TextFieldTableCell.<User>forTableColumn());
        nameColumn.setCellValueFactory(new PropertyValueFactory<User, String>(NAME_COLUMN_VALUE_FIELD_NAME));

        //roles column
        TableColumn<User, String> rolesColumn = new TableColumn<>(ROLE_COLUMN_TITLE);
        setColumnProperties(rolesColumn, columnWidth);
        rolesColumn.setCellFactory(TextFieldTableCell.<User>forTableColumn());
        rolesColumn.setCellValueFactory(new PropertyValueFactory<User, String>(ROLE_COLUMN_VALUE_FIELD_NAME));

        TableColumn<User, Boolean> statusColumn = new TableColumn<>(STATUS_COLUMN_TITLE);
        setColumnProperties(statusColumn, columnWidth);
        statusColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(cellData.getValue().getActive()));
        statusColumn.setCellFactory(col ->{
            return new TableCell<User, Boolean>(){
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (null == item || empty){
                    setText("");
                    } else {
                        if(item){
                            setText(STATUS_ACTIVE);
                            setTextFill(Color.color(STATUS_ACTIVE_RED_COLOR, STATUS_ACTIVE_GREEN_COLOR, STATUS_ACTIVE_BLUE_COLOR));
                        } else{
                            setText(STATUS_INACTIVE);
                            setTextFill(Color.color(STATUS_INACTIVE_RED_COLOR, STATUS_INACTIVE_GREEN_COLOR, STATUS_INACTIVE_BLUE_COLOR) );
                        }
                    }
                }
            };
        });


        //delete button column
        TableColumn<Product, Boolean> deleteButtonColumn = new TableColumn<>();
        setButtonColumnProperties(deleteButtonColumn, DELETE_COLUMN_TITLE);
        deleteButtonColumn.setCellFactory(new Callback<TableColumn<Product, Boolean>, TableCell<Product, Boolean>>() {
            @Override
            public TableCell<Product, Boolean> call(TableColumn<Product, Boolean> param) {

                DeleteButtonCell deleteButton = new DeleteButtonCell();
                deleteButton.createButton(genericTable, ManageUserController.super.getStageManager());

                return deleteButton;
            }
        });

        //add columns to tableView
        this.genericTable.getColumns().addAll(editButtonColumn, nameColumn, rolesColumn, statusColumn, deleteButtonColumn);
        super.getMainContentAnchor().getChildren().add(this.genericTable);
    }

    private void getDbData(){
        if (super.getStageManager().getSearchResults().size()!=0){
            this.genericTable.setItems(this.invokeBySearch(super.getStageManager().getSearchResults()));
            super.getStageManager().getSearchResults().clear();
        }  else {
            ObservableList<User> availableUser =  FXCollections.observableArrayList(this.userService.getAllRegisteredUsers());
            this.genericTable.setItems(availableUser);
        }
    }

    private <S> ObservableList<User> invokeBySearch(List<S> searchResults){
        List<User> availableUsers = (List<User>) searchResults;
        return FXCollections.observableArrayList(availableUsers);
    }

}
