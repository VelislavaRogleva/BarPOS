package app.controllers;

import app.dtos.UserDto;
import app.services.api.UserService;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class Controller_Not_Using {
    @FXML
    private TableView<UserDto> tableView;
    @FXML
    private TextField nameField;
    @FXML
    private TextField codeField;

    @Autowired
    private UserService userService;

    @FXML
    @Transactional
    protected void addUser(ActionEvent event) {
        ObservableList<UserDto> data = tableView.getItems();
        data.add(new UserDto(nameField.getText(), codeField.getText()));
        nameField.setText("");
        codeField.setText("");
    }
}
