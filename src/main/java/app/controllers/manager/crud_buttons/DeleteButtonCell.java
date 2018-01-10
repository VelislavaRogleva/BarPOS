package app.controllers.manager.crud_buttons;

import app.controllers.manager.manager_dialogs.ManagerDialogController;
import app.cores.StageManager;
import app.entities.Product;
import app.enums.ManagerEditDialogPath;
import app.enums.Pathable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class DeleteButtonCell<S> extends TableCell<Product, Boolean> {

    private static final String MANAGE_DELETE_DIALOG = "MANAGE_%s_DELETE_DIALOG";
    private Button deleteButton;
    private StageManager stageManager;

    @Autowired
    @Lazy
    public DeleteButtonCell(StageManager stageManager) {
        this.deleteButton = new Button();
        this.stageManager = stageManager;
    }
   public void createButton(TableView genericTable) {
        this.buttonProperties();
        this.deleteButton.setOnAction(event -> {
            int selectedIndex = getTableRow().getIndex();
            Object itemObject = genericTable.getItems().get(selectedIndex);
            String dialogPath = String.format(MANAGE_DELETE_DIALOG, itemObject.getClass().getSimpleName().toUpperCase());
            Pathable crudDialogPath = ManagerEditDialogPath.valueOf(dialogPath);
            showProductEditDialog(itemObject, crudDialogPath, genericTable, selectedIndex);
            genericTable.refresh();
        });
    }

    private <S> void showProductEditDialog(S deleteObject, Pathable viewPath, TableView genericTable, int selectedIndex){
        Parent deleteDialogParent = stageManager.getPane(viewPath);
        Stage deleteDialog = new Stage();
        deleteDialog.initStyle(StageStyle.UNDECORATED);

        //pop up window must be closed to continue interaction with the program
        deleteDialog.initModality(Modality.APPLICATION_MODAL);
        deleteDialog.setTitle("Delete");

        //set scene
        Scene dialogScene = new Scene(deleteDialogParent);
        deleteDialog.setScene(dialogScene);

        ManagerDialogController controller = this.stageManager.getController();
        controller.setDialogStage(deleteDialog);
        controller.setEditObject(deleteObject);
        controller.setTableView(genericTable);
        controller.setSelectedIndex(selectedIndex);

        deleteDialog.showAndWait();
    }

    private void buttonProperties() {
        this.deleteButton.getStyleClass().add("deleteButton");
        this.deleteButton.setText("DELETE");
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty){
            setGraphic(this.deleteButton);
        }
    }
}
