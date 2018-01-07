package app.controllers.manager.manager_dialogs;

import app.entities.Category;
import app.entities.Product;
import app.services.api.CategoryService;
import app.services.api.FieldValidationService;
import app.services.api.ImageUploadService;
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

    @Autowired
    public CategoryEditDialogController(CategoryService categoryService, FieldValidationService fieldValidationService) {
        this.categoryService = categoryService;
        this.fieldValidationService = fieldValidationService;
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
    public boolean isInputValid() {

        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(this.fieldValidationService.nameValidation(nameField.getText()));
        if (errorMessage.length() <=0){
            List<Category> allCategories = this.categoryService.getAllCategories();
            for (Category category:allCategories) {
                if (category.getName().equalsIgnoreCase(nameField.getText())){
                    errorMessage.append("The category exists. No override allowed!");
                }
            }
        }
        return this.fieldValidationService.validationErrorAlertBox(errorMessage, this.stage);
    }

    @Override
    public void removeObjectFromDB(Object object) {
        Category category = (Category) object;
        this.categoryService.remove(category);
        stage.close();
    }

    @FXML
    private void handleOk() {
        if (this.stage.getTitle().equalsIgnoreCase("Delete")){
            removeObjectFromDB(this.category);
        } else if (isInputValid()) {
            if (null == this.category){
                this.category = new Category();
            }

            this.category.setName(nameField.getText());

            if (titleLabel.getText().equals("Add")){
                this.table.getItems().add(0, category);
            }

            this.categoryService.save(this.category);
            this.table.refresh();
            stage.close();
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

}
