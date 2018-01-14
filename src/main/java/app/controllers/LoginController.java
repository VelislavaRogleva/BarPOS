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
    private static final int FADE_DUTRATION = 5;
    private static final int FADE_OUT_DURATION = 500;
    private static final String HOUR_PATTERN = "HH : mm";
    private static final String DATE_PATTERN = "dd.MM.YYYY";
    private static final String DAY_PATTERN = "EEEE";
    private static final String X_BUTTON_TITLE = "X";
    private static final int PASSKEY_FIELD_START_INDEX = 0;

    @FXML
   // private PasswordField passkeyField;
    private PasswordField passkeyField;
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
        this.registeredUsers = this.userService.getAllActiveUsers();
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
                            fadeTransition.setDuration(Duration.millis(FADE_DUTRATION));
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
        fadeTransition.setDuration(Duration.millis(FADE_DUTRATION));
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
        fadeTransition.setDuration(Duration.millis(FADE_OUT_DURATION));
        fadeTransition.setNode(this.VBoxUsersButtons);
        fadeTransition.setFromValue(MAX_OPACITY);
        fadeTransition.setToValue(MIN_OPACITY);

        fadeTransition.play();
    }

    private void timeInfo(){
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime localTime = LocalTime.now();
            LocalDate dateTime = LocalDate.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(HOUR_PATTERN, Locale.ENGLISH);
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.ENGLISH);
            DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern(DAY_PATTERN, Locale.ENGLISH);
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
            if (!buttonText.equals(X_BUTTON_TITLE)){
                this.passkeyField.setText(this.passkeyField.getText() + Integer.parseInt(buttonText));
            } else if (this.passkeyField.getLength()> 0) {
                this.passkeyField.setText(this.passkeyField.getText(PASSKEY_FIELD_START_INDEX, this.passkeyField.getLength()-1));
            }
        } else {
            this.passkeyField.setText("");
        }
    }

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
