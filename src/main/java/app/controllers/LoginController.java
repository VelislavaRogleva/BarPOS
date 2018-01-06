package app.controllers;

import app.cores.StageManager;
import app.entities.User;
import app.enums.ErrorMessages;
import app.enums.ViewPath;
import app.services.api.UserService;
import app.services.api.PassKeyVerificationService;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
public class LoginController implements FxmlController {

    private static final String[] KEYPAD_BUTTONS = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "X"};
    private static final int SHOW_REGISTERED_USERS = 3;

    private static final int KEY_PAD_COLUMNS = 3;

    private static final int MIN_OPACITY = 1;
    private static final int MAX_OPACITY = 0;

    private static final String KEY_PAD_BUTTON_STYLE_ID = "indexGridButton";
    private static final String USER_BUTTON_STYLE_ID = "indexUserButton";

    @FXML
   // private PasswordField passkeyField;
    private TextField passkeyField;
    @FXML private Label currentTime;
    @FXML private Label currentDate;
    @FXML private Label currentDay;
    @FXML private GridPane numPadPane;
    @FXML private VBox VBoxUsersButtons;

    private PassKeyVerificationService passKeyVerification;
    private UserService userService;
    private StageManager stageManager;
    private ToggleGroup toggleGroup;
    private List<User> registeredUsers;
    private int userButtonIndex;

    @Autowired
    @Lazy
    public LoginController(StageManager stageManager, UserService userService, PassKeyVerificationService passKeyVerification) {
        this.userButtonIndex = 0;
        this.stageManager = stageManager;
        this.userService = userService;
        this.registeredUsers = new ArrayList<>();
        this.passKeyVerification = passKeyVerification;
    }

    @Override
    public void initialize() {

//        ////////////////////////////////////////
//        //  Passwords id * 10
//        // gosho 1111111111
//        // pesho 2222222222
//        // ......
//        //////////////////////////////////////////
//
//        //from database must be List<User>
//
//        this.registeredUsers.add(new User(1L,"Gosho", "$2a$10$bjNBEn8NGtyUdVGW060bLeQ27TeRfWB.j6bEVVL6b9vYQbZrSE2G."));
//        this.registeredUsers.add(new User(2L,"Pesho", "$2a$10$yYfVZmHBYcgGGNQbSf6HsOPU0mrr2aHPjIlNFJZ3/IgxuZvkNS9SO"));
//        this.registeredUsers.add(new User(3L,"Stamat","$2a$10$2Ol5G6XSiulXBgwFmGz8pOd5zcN2sAC.iiQkecwpx133zxuKcOBZC"));
//        this.registeredUsers.add(new User(4L,"Pesho", "$2a$10$Ct0DUnELqmmuRpgkw0I/1.hUUCk1UXdEQtvTU/7xSnonk2zygxAtS"));
//        this.registeredUsers.add(new User(5L,"Besho","$2a$10$GglQNyfyqKCNy4kcZVuEUe52ESvovb5wiYpZIzRYnaufUuf7./g3K"));
//
/////////////////////////////////////////////////////////////////////////////////////////

        /*
        fetching users from database
        */
        //this.registeredUsers = this.userService.getAllRegisteredUsers();
        this.timeInfo();
        this.createUserBoxButtons();
        this.createKeyPad();
    }

    @FXML
    private void handleLoginButtonClick(){

        boolean isPasskeyFound = false;
        String inputPassKey = this.passkeyField.getText();
        String validateError = this.passKeyVerification.validatePassKey(inputPassKey);
        ToggleButton toggleButton = (ToggleButton) this.toggleGroup.getSelectedToggle();
        if (validateError.isEmpty()){
            if (toggleButton != null){
                long selectedUserId = Long.parseLong(toggleButton.getId());
                for (User user:this.registeredUsers) {
                    try {
                        if (user.getId() == selectedUserId && this.passKeyVerification.checkPassKey(inputPassKey, user.getPasswordHash())){

                            this.stageManager.setUser(user);
                            if (null == this.stageManager.getUser()) {
                                validateError = ErrorMessages.USER_NOT_FOUND.toString();
                                break;
                            }
                            isPasskeyFound = true;
                            FadeTransition fadeTransition = new FadeTransition();
                            fadeTransition.setDuration(Duration.millis(5));
                            fadeTransition.setNode(this.VBoxUsersButtons);
                            fadeTransition.setFromValue(MIN_OPACITY);
                            fadeTransition.setToValue(MAX_OPACITY);

                            fadeTransition.setOnFinished((ActionEvent event) -> {
                                this.stageManager.switchScene(ViewPath.SALE);
                            });
                            fadeTransition.play();
                            break;
                        }
                    } catch (RuntimeException e) {
                        validateError = e.getMessage();
                    }
                }
                validateError = isPasskeyFound ? "" : validateError.isEmpty() ? ErrorMessages.PASS_KEY_DO_NOT_MATCH.toString() : validateError;
            } else {
                validateError = isPasskeyFound && validateError.isEmpty() ? "" : ErrorMessages.SELECT_USER.toString();
            }
        }
        if(!validateError.isEmpty()){
            this.passkeyField.setText("");
            this.passkeyField.setPromptText(validateError);
        }
    }

    @FXML
    private void incrementScrollFade(){
        int registeredUsersCount = this.registeredUsers.size();
        if ((registeredUsersCount > SHOW_REGISTERED_USERS) && (this.userButtonIndex < registeredUsersCount - SHOW_REGISTERED_USERS) ){
            this.userButtonIndex++;
            makeFadeIn();
            makeFadeOut();
        }

    }

    @FXML
    private void decrementScrollFade(){
        if (this.userButtonIndex > 0){
            this.userButtonIndex--;
            makeFadeIn();
            makeFadeOut();
        }

    }

    private void makeFadeIn(){
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(5));
        fadeTransition.setNode(this.VBoxUsersButtons);
        fadeTransition.setFromValue(MIN_OPACITY);
        fadeTransition.setToValue(MAX_OPACITY);
        fadeTransition.setOnFinished((ActionEvent event) -> {

            this.VBoxUsersButtons.getChildren().clear();
            this.createUserBoxButtons();
        });
        fadeTransition.play();
    }

    private void makeFadeOut(){
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(500));
        fadeTransition.setNode(this.VBoxUsersButtons);
        fadeTransition.setFromValue(MAX_OPACITY);
        fadeTransition.setToValue(MIN_OPACITY);

        fadeTransition.play();
    }

    private void timeInfo(){
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime localTime = LocalTime.now();
            LocalDate dateTime = LocalDate.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH : mm", Locale.ENGLISH);
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.YYYY", Locale.ENGLISH);
            DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);
            this.currentTime.setText(localTime.format(timeFormatter));
            this.currentDate.setText(dateTime.format(dateFormat));
            this.currentDay.setText(dateTime.format(dayFormat));
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void createKeyPad(){
        for (int i = 0; i < KEYPAD_BUTTONS.length; i++)
        {
            Button button = new Button(KEYPAD_BUTTONS[i]);
            button.getStyleClass().add(KEY_PAD_BUTTON_STYLE_ID);
            if (!KEYPAD_BUTTONS[i].isEmpty()) {
                button.setId("" + i);
                button.setOnMouseClicked(e-> addPassKeyFieldValue(button));
            }
            this.numPadPane.add(button, i % KEY_PAD_COLUMNS, (int) Math.ceil(i / KEY_PAD_COLUMNS));
        }
    }

    private void addPassKeyFieldValue(Button button) {
        ToggleButton toggle = (ToggleButton) this.toggleGroup.getSelectedToggle();
        if (toggle != null){
            this.passkeyField.requestFocus();
            String buttonText = button.getText();
            if (!buttonText.equals("X")){
                this.passkeyField.setText(this.passkeyField.getText() + Integer.parseInt(buttonText));
            } else if (this.passkeyField.getLength()> 0) {
                this.passkeyField.setText(this.passkeyField.getText(0, this.passkeyField.getLength()-1));
            }
        } else {
            this.passkeyField.setText("");
        }
    }

    //TODO change TextField to PasswordField
    private void createUserBoxButtons(){

        this.toggleGroup = new ToggleGroup();
        this.toggleGroup.selectedToggleProperty().addListener((observable, toggleOld, toggleNew) -> {
            Toggle userToggle = this.toggleGroup.getSelectedToggle();
            this.passkeyField.setText("");
            if (userToggle !=null){
                this.passkeyField.requestFocus();
            }
        });

        int count = this.registeredUsers.size()>=SHOW_REGISTERED_USERS ? this.userButtonIndex + SHOW_REGISTERED_USERS : this.registeredUsers.size();
        for (int i = this.userButtonIndex; i < count; i++)
        {
            ToggleButton toggleButton = new ToggleButton(this.registeredUsers.get(i).getName());
            toggleButton.getStyleClass().add(USER_BUTTON_STYLE_ID);
            toggleButton.setSelected(false);
            toggleButton.setText(this.registeredUsers.get(i).getName());
            toggleButton.setId(String.valueOf(this.registeredUsers.get(i).getId()));
            toggleButton.setToggleGroup(this.toggleGroup);

            this.VBoxUsersButtons.getChildren().add(toggleButton);
        }
    }

}
