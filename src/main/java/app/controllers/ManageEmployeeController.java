package app.controllers;


import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;



@Component
public class ManageEmployeeController implements FxmlController {


    @FXML AnchorPane contentAnchor;

    public ManageEmployeeController() {

    }

    @Override
    public void initialize() {

    }
}
