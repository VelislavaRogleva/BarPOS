package app.controllers;


import app.entities.Category;
import app.services.api.CategoryService;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.soap.Text;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;


@Component
public class ManageCategoryController implements FxmlController {


   // @FXML private TableView<Category> contentTable;
    @FXML private Pane anchorPaneAddButton;
    @FXML private Pane mainContentAnchor;

    private TableView contentTable;


    private CategoryService categoryService;

    @Autowired
    public ManageCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Override
    public void initialize() {
        addButtonAction();
        createTable();

    }


    private void createTable() {


        this.contentTable = new TableView();
        contentTable.getStyleClass().addAll("contentTable");

        // create table columns
        TableColumn<Category, Boolean> editButton = new TableColumn<>();
        editButton.setMinWidth(60);
        editButton.setSortable(false);
        editButton.getStyleClass().addAll("editColumn");

        TableColumn<Category, String> categoryName = new TableColumn<>();
        categoryName.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryName.getStyleClass().addAll("contentColumn");

        TableColumn<Category, Boolean> deleteButton = new TableColumn<>();
        deleteButton.setMinWidth(75);
        deleteButton.setSortable(false);
        deleteButton.getStyleClass().addAll("deleteColumn");

        contentTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        contentTable.getColumns().clear();
        contentTable.getColumns().addAll(editButton, categoryName, deleteButton);

        //factory to show only if the column is not empty
        editButton.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Category, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Category, Boolean> param) {
                return new SimpleBooleanProperty(param.getValue() != null);
            }
        });
        deleteButton.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Category, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<Category, Boolean> param) {
                return new SimpleBooleanProperty(param.getValue() != null);
            }
        });

        //factory to add button into the column
        editButton.setCellFactory(new Callback<TableColumn<Category, Boolean>, TableCell<Category, Boolean>>() {
            @Override
            public TableCell<Category, Boolean> call(TableColumn<Category, Boolean> param) {
                return new EditButtonCell(contentTable);
            }
        });
        deleteButton.setCellFactory(new Callback<TableColumn<Category, Boolean>, TableCell<Category, Boolean>>() {
            @Override
            public TableCell<Category, Boolean> call(TableColumn<Category, Boolean> param) {
                return new DeleteButtonCell(contentTable);
            }
        });
        ObservableList<Category> categories = new ObservableListWrapper<>(this.categoryService.getAllCategories());
        contentTable.setItems(categories);
        this.mainContentAnchor.getChildren().addAll(contentTable);

    }

    ///////////////////////// dev creating fake database entries ////////////////////////////////
    private ObservableList<Category> getAllFakeCategories(){
        ObservableList<Category> categories = FXCollections.observableArrayList();
        String[] fakeCategories = {"coffee", "beer", "cocktails", "wine", "whiskey", "soft-drink", "brandy", "water", "tea", "nuts", "bacon"};
        Long id =1L;

        for (String category:fakeCategories) {
            Category newCat = new Category();
            newCat.setId(id);
            newCat.setName(category);
            categories.add(newCat);
            id++;
        }
        return categories;
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

    @FXML
    private void addButtonAction(){
        String name = this.getClass().getSimpleName().replace("Manage", "").replace("Controller", "").toUpperCase();
        Button button = new Button ("ADD " + name);
        button.getStyleClass().add("addButton");

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //creating new stage
                Stage editWindow = new Stage();

                //pop up window must be closed to continue interaction with the program
                editWindow.initModality(Modality.APPLICATION_MODAL);
                editWindow.setTitle("Add");

                Category currentCategory = new Category();

                //vbox for storing hboxes which will represent name and value of a single field of the Object
                VBox layout = new VBox();
                layout.setAlignment(Pos.CENTER);
                layout.setPadding(new Insets(20));

                drawFieldForEachProperty(currentCategory,layout);

                //create row for save and cancel buttons
                HBox buttonBox = new HBox();
                buttonBox.setAlignment(Pos.CENTER);
                buttonBox.setPadding(new Insets(20));
                buttonBox.setSpacing(40);
                Button saveButton = new Button("Save");

                //TODO save the new category to the database using categoryService
                saveButton.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {

                        //get Label text and corresponding  fieldValue
                        Map<String, String> editedValues = getFieldValue(layout);
                        applyNewValues(editedValues, currentCategory);
                        categoryService.save(currentCategory);
                        contentTable.getItems().add(currentCategory);
                        contentTable.refresh();
                        editWindow.close();
                    }
                });

                Button cancelButton = new Button("Cancel");
                cancelButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        editWindow.close();
                    }
                });
                buttonBox.getChildren().addAll(saveButton, cancelButton);
                layout.getChildren().add(buttonBox);
                //adding layout to the scene
                Scene scene = new Scene(layout);
                editWindow.setScene(scene);
                editWindow.showAndWait();
            }
        });

        this.anchorPaneAddButton.getChildren().add(button);


    }

    private class DeleteButtonCell extends TableCell<Category, Boolean> {

        Button delButton = new Button("DELETE");

        public DeleteButtonCell(TableView<Category> contentTable) {
            buttonProperties();
            delButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    int selectedIndex = getTableRow().getIndex();

                    //confirmation alert for deleting item
                    Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    deleteAlert.setTitle("Confirm deletion");
                    deleteAlert.setHeaderText("This will be permanently deleted");
                    deleteAlert.setContentText("Are you sure?");
                    Optional<ButtonType> result = deleteAlert.showAndWait();
                    if(result.get() == ButtonType.OK){
                        //delete item
                        Category category = contentTable.getItems().remove(selectedIndex);
                        categoryService.remove(category);
                        contentTable.refresh();
                    }
                }
            });

        }
        private void buttonProperties(){
            delButton.getStyleClass().add("deleteButton");
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty){
                setGraphic(delButton);
            }
        }
    }

    private class EditButtonCell extends TableCell<Category, Boolean> {
        Button edit = new Button("EDIT");

        public EditButtonCell(TableView<Category> contentTable) {
            buttonProperties();
            edit.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    int selectedIndex = getTableRow().getIndex();
                    //getting object which will be edited
                    Category currentCategory = contentTable.getItems().get(selectedIndex);

                    //creating new stage
                    Stage editWindow = new Stage();

                    //pop up window must be closed to continue interaction with the program
                    editWindow.initModality(Modality.APPLICATION_MODAL);
                    editWindow.setTitle("Edit");

                    //vbox for storing hboxes which will represent name and value of a single field of the Object
                    VBox layout = new VBox();
                    layout.setAlignment(Pos.CENTER);
                    layout.setPadding(new Insets(20));

                    drawFieldForEachProperty(currentCategory,layout);

                    //create row for save and cancer buttons
                    HBox buttonBox = new HBox();
                    buttonBox.setAlignment(Pos.CENTER);
                    buttonBox.setPadding(new Insets(20));
                    buttonBox.setSpacing(40);
                    Button saveButton = new Button("Save");
                    saveButton.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {

                            //get Label text and corresponding  fieldValue
                            Map<String, String> editedValues = getFieldValue(layout);
                            applyNewValues(editedValues, currentCategory);
                            categoryService.save(currentCategory);
                            contentTable.refresh();
                            editWindow.close();
                        }
                    });

                    Button cancelButton = new Button("Cancel");
                    cancelButton.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            editWindow.close();
                        }
                    });
                    buttonBox.getChildren().addAll(saveButton, cancelButton);
                    layout.getChildren().add(buttonBox);
                    //adding layout to the scene
                    Scene scene = new Scene(layout);
                    editWindow.setScene(scene);
                    editWindow.showAndWait();
                }
            });

        }

        private void buttonProperties(){
            edit.getStyleClass().add("editButton");
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            super.updateItem(item, empty);
            if (!empty){
                setGraphic(edit);
            }
        }
    }

    private void applyNewValues(Map<String, String> editedValues, Category currentCategory) {
        //changing to edited values
        for (Map.Entry<String, String> valueSet:editedValues.entrySet()) {
            try {
                //getting setter from edited Object
                Method[] meto = currentCategory.getClass().getDeclaredMethods();
                String methodName = "set" + valueSet.getKey().substring(0,1).toUpperCase() + valueSet.getKey().substring(1);
                Method currentMethod = currentCategory.getClass().getDeclaredMethod(methodName, java.lang.String.class);
                //invoke setter with new value
                currentMethod.invoke(currentCategory, valueSet.getValue());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

    }

    private Map<String,String> getFieldValue(VBox layout) {

        //contains Label value(needed to invoke setter), TextField value
        Map<String, String> editedValues = new LinkedHashMap<>();

        //Taking edited values from TextFields and their Labels
        outerLoop:
        for(Node node : layout.getChildren()){
            String labelName = "";
            String fieldValue = "";
            for(Node hBoxRow : ((HBox) node).getChildren()){
                String type = hBoxRow.getTypeSelector();
                //check for type to cast correctly
                switch(type){
                    case "Label":
                        Label currentLabel = ((Label)hBoxRow);
                        labelName = currentLabel.getText().replace(":","");
                        break;
                    case "TextField":
                        //TODO check for SQL injection
                        TextField currentTextField = (TextField)hBoxRow;
                        fieldValue = currentTextField.getText();
                        break;
                    case "Button":
                        break outerLoop;
                }
            }
            editedValues.put(labelName, fieldValue);
        }

        return editedValues;

    }

    private void drawFieldForEachProperty(Category currentCategory, VBox layout) {

        //getting all field of the object
        Field[] categoryFields = currentCategory.getClass().getDeclaredFields();
        for (Field field : categoryFields){

            field.setAccessible(true);
            //not acting if field is ID
            if (!field.getName().equals("id")){

                HBox row = new HBox();
                row.setAlignment(Pos.CENTER);
                row.setPadding(new Insets(20));
                row.setSpacing(5);

                //creating label for field name
                Label fieldName = new Label(field.getName()+ ":");
                fieldName.setId(field.getName());
                //creating textField for field value
                TextField fieldValue = new TextField();
                try {
                    fieldValue.setText((String) field.get(currentCategory));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                //add label and field to the row
                row.getChildren().addAll(fieldName, fieldValue);

                //add row to the main layout
                layout.getChildren().addAll(row);
            }

        }
    }
}
