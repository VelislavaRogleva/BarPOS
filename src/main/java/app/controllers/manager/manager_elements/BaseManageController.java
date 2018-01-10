package app.controllers.manager.manager_elements;

import app.controllers.FxmlController;
import app.controllers.manager.crud_buttons.AddButton;
import app.cores.StageManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class BaseManageController implements FxmlController {

    private static final Double BUTTON_DEFAULT_WIDTH = 90.0;
    private static final Double TABLE_DEFAULT_WIDTH = 790.0;
    private static final Double DELETE_BUTTON_OFFSET = 30.0;

    @FXML private Pane anchorPaneAddButton;
    @FXML private Pane mainContentAnchor;

    private StageManager stageManager;

    @Autowired
    @Lazy
    protected BaseManageController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    public Pane getAnchorPaneAddButton() {
        return this.anchorPaneAddButton;
    }

    public void setAnchorPaneAddButton(Pane anchorPaneAddButton) {
        this.anchorPaneAddButton = anchorPaneAddButton;
    }

    public void setMainContentAnchor(Pane mainContentAnchor) {
        this.mainContentAnchor = mainContentAnchor;
    }

    public StageManager getStageManager() {
        return this.stageManager;
    }

    public void setStageManager(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    abstract void createTable();

    Pane getMainContentAnchor() {
        return this.mainContentAnchor;
    }

    void setColumnProperties(TableColumn<?, ?> currentColumn, Double width) {
        currentColumn.getStyleClass().addAll("contentColumn");
        currentColumn.setPrefWidth(width);
        currentColumn.setMinWidth(width);
        currentColumn.setMaxWidth(width);
    }

    void setButtonColumnProperties(TableColumn<?, Boolean> buttonColumn, String buttonStyleClass) {
        buttonColumn.setPrefWidth(BUTTON_DEFAULT_WIDTH);
        buttonColumn.setMaxWidth(BUTTON_DEFAULT_WIDTH);
        buttonColumn.setMinWidth(BUTTON_DEFAULT_WIDTH);
        buttonColumn.setSortable(false);
        buttonColumn.getStyleClass().addAll(buttonStyleClass);
    }

    double calculateColumnWidth(double columnCount){
        return (TABLE_DEFAULT_WIDTH - (2*BUTTON_DEFAULT_WIDTH) - DELETE_BUTTON_OFFSET)/ columnCount;
    }

    @FXML
    <S> void addButtonAction(TableView genericTable){

        String name = this.getClass().getSimpleName().replace("Manage", "").replace("Controller", "").toUpperCase();
        String entityName = String.format("%s%s",name.substring(0,1),name.substring(1).toLowerCase());
        AddButton<S> newAddButton = new AddButton<>(this.stageManager);
        Button addButton = newAddButton.createButton(entityName, genericTable);
        this.anchorPaneAddButton.getChildren().add(addButton);
    }
}
