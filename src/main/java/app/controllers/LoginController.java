package app.controllers;

import app.dev.StageManager;
import app.entities.User;
import app.enums.ViewMap;
import app.factory.SceneFactory;
import app.services.password_service.PassKeyVerification;
import app.services.password_service.PassKeyVerificationService;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
public class LoginController implements FxmlController {

    private static final String[] KEYPAD_BUTTONS = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "", "0", "X"};
    private static final int SHOW_REGISTERED_USERS = 3;

    private static final int KEY_PAD_BUTTONS_COUNT = 12;
    private static final int KEY_PAD_COLUMNS = 3;

    private static final int MIN_OPACITY = 1;
    private static final int MAX_OPACITY = 0;
    private static final String KEY_PAD_BUTTON_STYLE_ID = "indexGridButton";

    @FXML
   // private PasswordField passkeyField;
    private TextField passkeyField;
    @FXML private Label currentTime;
    @FXML private Label currentDate;
    @FXML private Label currentDay;
    @FXML private GridPane numPadPane;
    @FXML private VBox VBoxUsersButtons;

    private StageManager stageManager;
    private PassKeyVerification passKeyVerificationService;
    private ToggleGroup toggleGroup;
    private List<User> registeredUsers;
    private int startShowIndex;

    @Autowired
    @Lazy
    public LoginController(PassKeyVerification passKeyVerification,  StageManager stageManager) {
        this.startShowIndex = 0;
        this.stageManager = stageManager;
        this.passKeyVerificationService = passKeyVerification;


 //////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////
        //  Passwords id * 10
        // gosho 1111111111
        // pesho 2222222222
        // ......
        //////////////////////////////////////////

        //from database must be List<User>
        this.registeredUsers = new ArrayList<>();
        this.registeredUsers.add(new User(1L,"Gosho", "$2a$10$bjNBEn8NGtyUdVGW060bLeQ27TeRfWB.j6bEVVL6b9vYQbZrSE2G."));
        this.registeredUsers.add(new User(2L,"Pesho", "$2a$10$yYfVZmHBYcgGGNQbSf6HsOPU0mrr2aHPjIlNFJZ3/IgxuZvkNS9SO"));
        this.registeredUsers.add(new User(3L,"Stamat","$2a$10$2Ol5G6XSiulXBgwFmGz8pOd5zcN2sAC.iiQkecwpx133zxuKcOBZC"));
        this.registeredUsers.add(new User(4L,"Pesho", "$2a$10$Ct0DUnELqmmuRpgkw0I/1.hUUCk1UXdEQtvTU/7xSnonk2zygxAtS"));
        this.registeredUsers.add(new User(5L,"Besho","$2a$10$GglQNyfyqKCNy4kcZVuEUe52ESvovb5wiYpZIzRYnaufUuf7./g3K"));

///////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    public void initialize() {

        this.timeInfo();
        this.createUserBoxButtons();
        this.createKeyPad();
    }

    public void handleLoginButtonClick(){

        boolean isPasskeyFound = false;

        String inputPassword = this.passkeyField.getText();
        String validateError = passKeyVerificationService.validatePassword(inputPassword);
        ToggleButton toggleButton = (ToggleButton) this.toggleGroup.getSelectedToggle();
//        String hashedPass = verifyPassword.hashPassword(this.passkeyField.getText());
//        System.out.println(hashedPass);
        if (validateError.isEmpty()){
            if (toggleButton != null){
                long selectedUserId = Long.parseLong(toggleButton.getId());
                for (User user:this.registeredUsers) {
                    if (user.getId() == selectedUserId && passKeyVerificationService.checkPassword(inputPassword, user.getPasswordHash())){

                        stageManager.setUser(user);
                        if (null == stageManager.getUser()) {
                            validateError = "User not found";
                            break;
                        }
                        isPasskeyFound = true;
                        FadeTransition fadeTransition = new FadeTransition();
                        fadeTransition.setDuration(Duration.millis(5));
                        fadeTransition.setNode(this.VBoxUsersButtons);
                        fadeTransition.setFromValue(MIN_OPACITY);
                        fadeTransition.setToValue(MAX_OPACITY);

                        fadeTransition.setOnFinished((ActionEvent event) -> {
                            stageManager.switchScene(ViewMap.TABLE);
                        });
                        fadeTransition.play();
                        break;
                    }
                }
                validateError = isPasskeyFound ? "" : validateError.isEmpty() ? "passkey do not match" : validateError;
            } else {
                validateError = isPasskeyFound && validateError.isEmpty() ? "" : "please select user";
            }
        }
        if(!validateError.isEmpty()){
            this.passkeyField.setText("");
            this.passkeyField.setPromptText(validateError);
        }
    }

    public void incrementScrollFade(){
        if (startShowIndex != this.registeredUsers.size() - SHOW_REGISTERED_USERS ){
            this.startShowIndex++;
            makeFadeIn();
        }
        makeFadeOut();
    }

    public void decrementScrollFade(){
        if (this.startShowIndex !=0){
            this.startShowIndex--;
            makeFadeIn();
        }
        makeFadeOut();
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
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH : mm");
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.YYYY");
            DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("EEEE");
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
            button.getStyleClass().add("indexGridButton");
            if (!KEYPAD_BUTTONS[i].isEmpty()) {
                button.setId("" + i);
                button.setOnMouseClicked(e-> {
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
                });
            }
            this.numPadPane.add(button, i % KEY_PAD_COLUMNS, (int) Math.ceil(i / KEY_PAD_COLUMNS));
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

        int count = this.registeredUsers.size()>=SHOW_REGISTERED_USERS ? startShowIndex + SHOW_REGISTERED_USERS : this.registeredUsers.size();

        for (int i = this.startShowIndex; i < count; i++)
        {
            ToggleButton toggleButton = new ToggleButton(this.registeredUsers.get(i).getName());
            toggleButton.getStyleClass().add("indexUserButton");
            toggleButton.setSelected(false);
            toggleButton.setText(this.registeredUsers.get(i).getName());
            toggleButton.setId(String.valueOf(this.registeredUsers.get(i).getId()));
            toggleButton.setToggleGroup(this.toggleGroup);

            this.VBoxUsersButtons.getChildren().add(toggleButton);
        }
    }

}
