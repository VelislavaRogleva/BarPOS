package app.controllers.manager.manager_dialogs;

import app.cores.StageManager;
import app.entities.Product;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DeleteButtonCell<S> extends TableCell<Product, Boolean> {

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
        this.deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int selectedIndex = getTableRow().getIndex();

                //confirmation alert for deleting item
                    Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    //deleteAlert.initStyle(StageStyle.UNDECORATED);
                    deleteAlert.setTitle("Confirm deletion");
                    deleteAlert.setHeaderText("This will be permanently deleted");
                    deleteAlert.setContentText("Are you sure?");
                    Optional<ButtonType> result = deleteAlert.showAndWait();
                    if(result.get() == ButtonType.OK){
                        //delete item
                    genericTable.getItems().remove(selectedIndex);
                    genericTable.refresh();
                    }
            }
        });
    }

    void buttonProperties() {
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
