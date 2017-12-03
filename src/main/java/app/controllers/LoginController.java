package app.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LoginController {

    @FXML
    private PasswordField passField;
    @FXML
    private Text textLogin;
    @FXML
    private Text textClock;
    @FXML
    private Text textDate;
    @FXML
    private Text textDay;
    @FXML
    private VBox hboxUsers;


    @FXML
    private void initialize() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime localTime = LocalTime.now();
            LocalDate dateTime = LocalDate.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.YYYY");
            textClock.setText(localTime.format(timeFormatter));
            textDate.setText(dateTime.format(dateFormat));
            textDay.setText(dateTime.getDayOfWeek().name());
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    @FXML
    private void handleNumberButton(ActionEvent event) {
        String input = ((Button)event.getSource()).getText();
        if (input.equals("DEL")) {
            if (!passField.getText().isEmpty())
                passField.deleteText(passField.getLength() - 1, passField.getLength());
        }
        else
            passField.appendText(input);
    }

    @FXML
    private void handleLoginButton() {

    }
}
