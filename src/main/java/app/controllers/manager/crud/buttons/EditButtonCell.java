package app.controllers.manager.crud.buttons;

import app.controllers.manager.crud.dialogs.ManagerDialogController;
import app.cores.StageManager;
import app.enums.Pathable;
import app.enums.ViewElementPath;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class EditButtonCell<S> extends TableCell<S, Boolean> {

    private static final String MANAGE_EDIT_DIALOG = "MANAGE_%s_EDIT_DIALOG";
    private Button editButton;
    private StageManager stageManager;

    public EditButtonCell() {
    }

    //    @Autowired
//    @Lazy
//    public EditButtonCell(StageManager stageManager) {
//        this.stageManager = stageManager;
//    }

    public void createButton(TableView genericTable, StageManager stageManager) {
        this.stageManager = stageManager;
        this.editButton = new Button();
        buttonProperties();

        this.editButton.setOnAction(event -> {
            int selectedIndex = getTableRow().getIndex();
            //getting object which will be edited
            S currentObject = (S) genericTable.getItems().get(selectedIndex);
            String dialogPath = String.format(MANAGE_EDIT_DIALOG,currentObject.getClass().getSimpleName().toUpperCase());
            Pathable crudDialogPath = ViewElementPath.valueOf(dialogPath);
            showProductEditDialog(currentObject, crudDialogPath, genericTable);
            genericTable.refresh();
        });
    }

    private void showProductEditDialog(S editObject, Pathable viewPath, TableView genericTable){

            Parent editDialogParent = stageManager.getPane(viewPath);
            Stage editDialog = new Stage();
            editDialog.initStyle(StageStyle.UNDECORATED);

            //pop up window must be closed to continue interaction with the program
            editDialog.initModality(Modality.APPLICATION_MODAL);
            editDialog.setTitle("Edit");

            //set scene
            Scene dialogScene = new Scene(editDialogParent);
            editDialog.setScene(dialogScene);

            ManagerDialogController controller = this.stageManager.getController();
            controller.setDialogStage(editDialog);
            controller.setEditObject(editObject);
            controller.setTableView(genericTable);

            editDialog.showAndWait();
    }

    @Override
    protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (!empty){
            setGraphic(editButton);
        }
    }

    private void buttonProperties(){
        this.editButton.getStyleClass().add("editButton");
        this.editButton.setText("EDIT");
    }

}
