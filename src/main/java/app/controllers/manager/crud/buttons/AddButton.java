package app.controllers.manager.crud.buttons;

import app.controllers.manager.crud.dialogs.ManagerDialogController;
import app.cores.StageManager;
import app.enums.Pathable;
import app.enums.ViewElementPath;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Controller
public class AddButton {

    private static final String OBJECT_PATH = "app.entities.";
    private static final String MANAGE_EDIT_DIALOG = "MANAGE_%s_EDIT_DIALOG";

    private Button addButton;
    private StageManager stageManager;

    @Autowired
    @Lazy
    public AddButton(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    public <S> Button createButton(String entityName, TableView genericTable) {
        this.addButton = new Button();
        buttonProperties(entityName);

        this.addButton.setOnAction(event -> {
            //getting the object which will be edited
            Class<?> objectClass = null;
            try {
                objectClass = Class.forName(OBJECT_PATH + entityName);
                Constructor<?> objectConstructor = objectClass.getDeclaredConstructor();
                S newObject = (S) objectConstructor.newInstance();
                String dialogPath = String.format(MANAGE_EDIT_DIALOG, newObject.getClass().getSimpleName().toUpperCase());
                Pathable crudDialogPath = ViewElementPath.valueOf(dialogPath);
                showProductEditDialog(newObject, crudDialogPath, genericTable);
                genericTable.refresh();

            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                //alert.initOwner(stage);
                alert.setTitle("Bad Action");
                alert.setHeaderText("Cannot perform action");
                alert.setContentText("Your action cannot be completed!");
                alert.showAndWait();
            }

        });
        return this.addButton;
    }

    private <S> void showProductEditDialog(S editObject, Pathable viewPath, TableView genericTable){


        Parent editDialogParent = stageManager.getPane(viewPath);
        Stage editDialog = new Stage();
        editDialog.initStyle(StageStyle.UNDECORATED);

        //pop up window must be closed to continue interaction with the program
        editDialog.initModality(Modality.APPLICATION_MODAL);
        //editDialog.initModality(Modality.WINDOW_MODAL);
        editDialog.setTitle("Add");

        //set scene
        Scene dialogScene = new Scene(editDialogParent);
        editDialog.setScene(dialogScene);

        ManagerDialogController controller = this.stageManager.getController();
        controller.setDialogStage(editDialog);
        controller.setEditObject(null);
        //controller.setEditObject(editObject);
        controller.setTableView(genericTable);

        editDialog.showAndWait();
    }


    private void buttonProperties(String entityName) {
        String buttonName = this.getClass().getSimpleName().replace("Button", "");
        this.addButton.getStyleClass().add("addButton");
        this.addButton.setText(String.format("ADD %s",entityName.toUpperCase()));
    }
}
