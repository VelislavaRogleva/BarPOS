package app.controllers.manager;

import app.controllers.manager.crud.buttons.DeleteButtonCell;
import app.controllers.manager.crud.buttons.EditButtonCell;
import app.cores.StageManager;
import app.entities.Category;
import app.entities.Product;
import app.entities.User;
import app.services.api.CategoryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ManageCategoryController extends BaseManageController {

    private static final int OBJECT_COUNT_PROPERTIES = 1;
    private static final String GENERIC_TABLE_STYLE_CLASS_NAME = "contentTable";
    private static final String EDIT_COLUMN_TITLE = "editColumn";
    private static final String NAME_COLUMN_TITLE = "name";
    private static final String NAME_COLUMN_VALUE_FIELD_NAME = "name";
    private static final String DELETE_COLUMN_TITLE = "deleteColumn";

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
            editButton.createButton(genericTable, ManageCategoryController.super.getStageManager());
            return editButton;
        });

        //name column
        TableColumn<User, String> nameColumn = new TableColumn<>(NAME_COLUMN_TITLE);
        setColumnProperties(nameColumn, columnWidth);
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setCellValueFactory(new PropertyValueFactory<>(NAME_COLUMN_VALUE_FIELD_NAME));

        //delete button column
        TableColumn<Product, Boolean> deleteButtonColumn = new TableColumn<>();
        setButtonColumnProperties(deleteButtonColumn, DELETE_COLUMN_TITLE);
        deleteButtonColumn.setCellFactory(param -> {

            DeleteButtonCell deleteButton = new DeleteButtonCell();
            deleteButton.createButton(genericTable, ManageCategoryController.super.getStageManager());
            return deleteButton;
        });

        this.genericTable.getColumns().addAll(editButtonColumn, nameColumn, deleteButtonColumn);
        ObservableList<Category> availableCategory = FXCollections.observableArrayList(this.categoryService.getAllCategories());
        this.genericTable.setItems(availableCategory);
        super.getMainContentAnchor().getChildren().add(this.genericTable);
    }

    private void getDbData(){
        if (super.getStageManager().getSearchResults().size()!=0){
            this.genericTable.setItems(this.invokeBySearch(super.getStageManager().getSearchResults()));
            super.getStageManager().getSearchResults().clear();
        }  else {
            ObservableList<Category> availableCategories =  FXCollections.observableArrayList(this.categoryService.getAllCategories());
            this.genericTable.setItems(availableCategories);
        }
    }

    private <S> ObservableList<Category> invokeBySearch(List<S> searchResults){
        List<Category> availableCategories = (List<Category>) searchResults;
        return FXCollections.observableArrayList(availableCategories);
    }
}
