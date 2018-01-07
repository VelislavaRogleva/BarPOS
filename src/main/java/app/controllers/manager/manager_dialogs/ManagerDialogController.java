package app.controllers.manager.manager_dialogs;

import app.controllers.FxmlController;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public interface ManagerDialogController extends FxmlController {

    void setDialogStage(Stage dialogStage);

    void setTableView(TableView tableView);

    <S> void setEditObject(S editObject);

    boolean isInputValid();

    void removeObjectFromDB(Object object);
}
