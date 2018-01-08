package app.controllers;

import app.controllers.FxmlController;
import app.cores.StageManager;
import app.entities.User;
import app.enums.Pathable;
import app.enums.ViewElementPath;
import app.enums.ViewPath;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class ManagerController implements FxmlController {

    private static final String ROLE_AUTHORIZATION = "admin";

    private static final String[] AVAILABLE_CHOICES = {"name", "price", "category", "quantity", "role"};
    private static final String DEFAULT_SELECTED_BUTTON = "sale";

    @FXML private Label currentTime;
    @FXML private Label currentUser;
    @FXML private BorderPane contentPane;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> comboBox;
    @FXML private Pane leftMenuPane;

    private ToggleGroup menuButtonsGroup;
    private StageManager stageManager;


    @Autowired
    @Lazy
    public ManagerController(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @Override
    public void initialize() {
  //      this.roleAuthorization();
        this.createMenuButtons();
        this.addChoices();
        this.timeInfo();
        if (null == this.stageManager.getUser()){
            this.currentUser.setText("Welcome");
        } else {
            this.currentUser.setText(this.stageManager.getUser().getName());
        }

    }


    public void roleAuthorization() {
        User user = this.stageManager.getUser();
        if (!ROLE_AUTHORIZATION.equalsIgnoreCase(user.getRole())){
            this.stageManager.switchScene(ViewPath.LOGIN);
        }
    }

    private void createMenuButtons(){

        this.menuButtonsGroup = new ToggleGroup();

        //add listener to check if we have selected button and to load the action from onAction method
        this.menuButtonsGroup.selectedToggleProperty().addListener((observable, toggleOld, toggleNew) -> {

            Toggle userToggle = this.menuButtonsGroup.getSelectedToggle();
            if (userToggle !=null){

                ToggleButton selectedButton = (ToggleButton) userToggle;
                Parent parent = this.stageManager.getPane(ViewElementPath.valueOf(selectedButton.getId()));
                this.contentPane.setCenter(parent);
            }
        });

        //creating toggleButtons for each element in the enum ViewElementPath
        Pathable[] viewElements = ViewElementPath.values();
        for (Pathable element : viewElements)
        {
            String enumName = String.valueOf(element);
            long count = enumName.codePoints().filter(s -> s == '_').count();
            if (count > 1){
                continue;
            }
            ToggleButton toggleButton = new ToggleButton();

            //extracting button styleId from enum
            String viewName = enumName.substring(enumName.indexOf("_")+1).toLowerCase();
            String styleId = viewName + "Button";

            toggleButton.getStyleClass().add(styleId);

            //setting Sale button to be selected on initial load of Manager.fxml
            if (DEFAULT_SELECTED_BUTTON.equals(viewName)){
                toggleButton.setSelected(true);
                toggleButton.requestFocus();
            }else {
                toggleButton.setSelected(false);
            }
            toggleButton.setId(enumName);
            toggleButton.setToggleGroup(menuButtonsGroup);

            //when button is clicked, stateManager will load the correct content in the middle contentPane
            toggleButton.setOnAction(event -> {
                Parent parent = stageManager.getPane(ViewElementPath.valueOf(enumName));
                contentPane.setCenter(parent);
            });
            this.leftMenuPane.getChildren().add(toggleButton);
        }
    }

    @FXML
    private void logout(){
        this.stageManager.switchScene(ViewPath.LOGIN);
    }

    @FXML
    private void searchButtonOnClick(){
        System.out.println(this.getSearch()[0]);
        System.out.println(this.getSearch()[1]);
    }

    @FXML
    private void backToUserPane(){
        this.stageManager.switchScene(ViewPath.SALE);
    }

    private void addChoices() {
        this.comboBox.getItems().addAll(AVAILABLE_CHOICES);
        this.comboBox.getSelectionModel().selectFirst();
    }

    private String[] getSearch(){
        String[] searchValues = new String[2];
        searchValues[0]= this.searchField.getText();
        searchValues[1]= this.comboBox.getValue();
        return  searchValues;
    }

    private void timeInfo(){
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime localTime = LocalTime.now();
            LocalDate dateTime = LocalDate.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM ", Locale.ENGLISH);
            this.currentTime.setText(dateTime.format(dateFormat) + localTime.format(timeFormatter));
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
}
