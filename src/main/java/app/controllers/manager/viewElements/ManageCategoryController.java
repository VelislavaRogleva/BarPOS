package app.controllers.manager.viewElements;

import app.controllers.manager.manager_dialogs.DeleteButtonCell;
import app.controllers.manager.manager_dialogs.EditButtonCell;
import app.cores.StageManager;
import app.entities.Category;
import app.entities.Product;
import app.entities.Role;
import app.entities.User;
import app.services.api.CategoryService;
import app.services.api.PassKeyVerificationService;
import app.services.api.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;


@Component
public class ManageCategoryController extends BaseManageController {

    private static final int OBJECT_COUNT_PROPERTIES = 1;

    private CategoryService categoryService;
    private TableView genericTable;

    @Autowired
    @Lazy
    public ManageCategoryController(StageManager stageManager, CategoryService categoryService) {
        super(stageManager);
        this.categoryService = categoryService;
    }

    @Override
    public void initialize() {
        createTable();
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
                EditButtonCell editButton = new EditButtonCell(ManageCategoryController.super.getStageManager());
                editButton.createButton(genericTable);
                return editButton;
            }
        });

        //name column
        TableColumn<User, String> nameColumn = new TableColumn<>("name");
        setColumnProperties(nameColumn, columnWidth);
        nameColumn.setCellFactory(TextFieldTableCell.<User>forTableColumn());
        nameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("name"));

        //delete button column
        TableColumn<Product, Boolean> deleteButtonColumn = new TableColumn<>();
        setButtonColumnProperties(deleteButtonColumn, "deleteColumn");
        deleteButtonColumn.setCellFactory(new Callback<TableColumn<Product, Boolean>, TableCell<Product, Boolean>>() {
            @Override
            public TableCell<Product, Boolean> call(TableColumn<Product, Boolean> param) {
                DeleteButtonCell deleteButton = new DeleteButtonCell(ManageCategoryController.super.getStageManager());
                deleteButton.createButton(genericTable);
                return deleteButton;
            }
        });

        this.genericTable.getColumns().addAll(editButtonColumn, nameColumn, deleteButtonColumn);
        ObservableList<Category> availableCategory = FXCollections.observableArrayList(this.categoryService.getAllCategories());
        this.genericTable.setItems(availableCategory);
        super.getMainContentAnchor().getChildren().add(this.genericTable);
    }
}
