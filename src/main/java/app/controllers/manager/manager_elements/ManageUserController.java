package app.controllers.manager.manager_elements;

import app.controllers.manager.crud_buttons.DeleteButtonCell;
import app.controllers.manager.crud_buttons.EditButtonCell;
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
                editButton.createButton(genericTable, ManageUserController.super.getStageManager());
                return editButton;
            }
        });

        //name column
        TableColumn<User, String> nameColumn = new TableColumn<>("name");
        setColumnProperties(nameColumn, columnWidth);
        nameColumn.setCellFactory(TextFieldTableCell.<User>forTableColumn());
        nameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("name"));

        //roles column
        TableColumn<User, String> rolesColumn = new TableColumn<>("role");
        setColumnProperties(rolesColumn, columnWidth);
        rolesColumn.setCellFactory(TextFieldTableCell.<User>forTableColumn());
        rolesColumn.setCellValueFactory(new PropertyValueFactory<User, String>("role"));

        TableColumn<User, Boolean> statusColumn = new TableColumn<>("status");
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
                            setText("active");
                            setTextFill(Color.color(0.54, 0.67, 0.09));
                        } else{
                            setText("inactive");
                            setTextFill(Color.color(0.69, 0.047, 0.18) );
                        }
                    }
                }
            };
        });


        //delete button column
        TableColumn<Product, Boolean> deleteButtonColumn = new TableColumn<>();
        setButtonColumnProperties(deleteButtonColumn, "deleteColumn");
        deleteButtonColumn.setCellFactory(new Callback<TableColumn<Product, Boolean>, TableCell<Product, Boolean>>() {
            @Override
            public TableCell<Product, Boolean> call(TableColumn<Product, Boolean> param) {
//                DeleteButtonCell deleteButton = new DeleteButtonCell(ManageUserController.super.getStageManager());
//                deleteButton.createButton(genericTable);

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
