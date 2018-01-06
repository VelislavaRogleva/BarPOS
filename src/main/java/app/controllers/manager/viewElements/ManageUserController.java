package app.controllers.manager.viewElements;

import app.controllers.manager.manager_dialogs.DeleteButtonCell;
import app.controllers.manager.manager_dialogs.EditButtonCell;
import app.cores.StageManager;
import app.entities.Product;
import app.entities.Role;
import app.entities.User;
import app.services.api.PassKeyVerificationService;
import app.services.api.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class ManageUserController extends BaseManageController {

    private static final int OBJECT_COUNT_PROPERTIES = 2;

//    @FXML private URL location;


    private UserService userService;
    ////////////////////////////////////////////////////////
    //TODO delete this!! only for dev
    private PassKeyVerificationService categoryService;
    //////////////////////////////////////////////////////////
    private TableView genericTable;

    @Autowired
    @Lazy
    public ManageUserController(StageManager stageManager, UserService userService, PassKeyVerificationService categoryService) {
        super(stageManager);
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @Override
    public void initialize() {
        createTable();
        super.addButtonAction(this.genericTable);
    }

    ///////////////////////// dev creating fake database entries ////////////////////////////////
    // Set
    protected <S> ObservableList<S> getAllFakeCategories(){
        ObservableList<S> categories = FXCollections.observableArrayList();

        String[] fakeCategories = {"coffee", "beer", "cocktails", "wine", "whiskey"};
        Long id =1L;

        for (String category:fakeCategories) {
            User newCat = new User();
            newCat.setId(id);
            newCat.setName(category);
            newCat.setPasswordHash(this.categoryService.hashPassKey(String.format("bobotopop%d",id)));
            Role role0 = new Role();
            role0.setId(1111L);
            String roleString = id % 2 == 0 ? "MANAGER" : "WAITER";
            role0.setRole(roleString);
            newCat.setRole(role0);
            categories.add((S) newCat);
            id++;
        }
        return categories;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////



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
                EditButtonCell editButton = new EditButtonCell(ManageUserController.super.getStageManager());
                editButton.createButton(genericTable);
                return editButton;
            }
        });

        //name column
        TableColumn<User, String> nameColumn = new TableColumn<>("name");
        setColumnProperties(nameColumn, columnWidth);
        nameColumn.setCellFactory(TextFieldTableCell.<User>forTableColumn());
        nameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("name"));

        //roles column
        TableColumn<User, Role> rolesColumn = new TableColumn<>("role");
        setColumnProperties(rolesColumn, columnWidth);
        rolesColumn.setCellFactory(rc -> new TableCell<User, Role>(){
            @Override
            public void updateItem(Role roles, boolean empty){
                super.updateItem(roles, empty);
                if (empty){
                    setText("");
                } else {
                    setText(roles.getRole());
                }
            }
        });
        rolesColumn.setCellValueFactory(new PropertyValueFactory<User, Role>("role"));

        //delete button column
        TableColumn<Product, Boolean> deleteButtonColumn = new TableColumn<>();
        setButtonColumnProperties(deleteButtonColumn, "deleteColumn");
        deleteButtonColumn.setCellFactory(new Callback<TableColumn<Product, Boolean>, TableCell<Product, Boolean>>() {
            @Override
            public TableCell<Product, Boolean> call(TableColumn<Product, Boolean> param) {
                DeleteButtonCell deleteButton = new DeleteButtonCell(ManageUserController.super.getStageManager());
                deleteButton.createButton(genericTable);
                return deleteButton;
            }
        });

        //add columns to tableView
        this.genericTable.getColumns().addAll(editButtonColumn, nameColumn, rolesColumn, deleteButtonColumn);

        // fetch from database
        //ObservableList<User> availableEmployees = FXCollections.observableArrayList(this.userService.getAllRegisteredUsers());

        //get from fakeLand
        ObservableList<User> availableEmployees = getAllFakeCategories();
        if (availableEmployees.size()>0) {
            this.genericTable.setItems(availableEmployees);
            super.getMainContentAnchor().getChildren().add(this.genericTable);
        }
    }

}
