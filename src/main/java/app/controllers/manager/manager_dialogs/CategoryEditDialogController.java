package app.controllers.manager.manager_dialogs;

import app.entities.Category;
import app.entities.Product;
import app.services.api.CategoryService;
import app.services.api.FieldValidationService;
import app.services.api.ImageUploadService;
import app.services.api.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Component
public class CategoryEditDialogController implements ManagerDialogController {

    @FXML private Label titleLabel;
    @FXML private TextField nameField;

    private Category category;
    private Stage stage;
    private TableView table;
    private CategoryService categoryService;
    private FieldValidationService fieldValidationService;
    private ProductService productService;
    private int selectedIndex;

    @Autowired
    public CategoryEditDialogController(CategoryService categoryService, FieldValidationService fieldValidationService, ProductService productService) {
        this.categoryService = categoryService;
        this.fieldValidationService = fieldValidationService;
        this.productService = productService;
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
    public <S> void setEditObject(S category) {
        this.category = (Category) category;
        titleLabel.setText(this.stage.getTitle());

        switch(this.stage.getTitle()){
            case "Delete":
                break;
            case "Edit":
                nameField.setText(this.category.getName());
                break;
            default:
                break;
        }
    }

    @Override
    public void setSelectedIndex(int index){
        this.selectedIndex = index;
    }

    @Override
    public boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(this.fieldValidationService.nameTypeValidation(nameField.getText(), nameField.getPromptText()));
        List<Category> allCategories = this.categoryService.getAllCategories();
        errorMessage.append(this.fieldValidationService.categoryNameMatchValidation(allCategories, nameField.getText()));
        return errorMessage.length() < 1 || this.fieldValidationService.validationErrorAlertBox(errorMessage.toString(), this.stage);
    }

    private <S> void removeObjectFromDB() {
        List<Product> productsByCategory = this.productService.getProductsByCategory(this.category);
        if (productsByCategory.size() <= 0){
            this.categoryService.remove(category);
            this.table.getItems().remove(this.selectedIndex);
        } else {
            this.fieldValidationService.validationErrorAlertBox("Cannot delete non empty category!", this.stage);
        }
    }

    @FXML
    private void handleOk() {
        if (this.stage.getTitle().equalsIgnoreCase("Delete")){
            removeObjectFromDB();
            stage.close();
        } else if (isInputValid()) {
            if (null == this.category){
                this.category = new Category();
            }
            this.category.setName(nameField.getText());

            int tableSize = this.table.getChildrenUnmodifiable().size();
            if (titleLabel.getText().equals("Add")){
                this.table.getItems().add(0, category);
            }

            this.categoryService.save(this.category);
            stage.close();
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

}
