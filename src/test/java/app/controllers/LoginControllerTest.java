package app.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginControllerTest {

    @Autowired
    private FxmlController loginController;


    @Before
    public void init() throws Exception {
        String a = "";
    }

    @Test
    public void initialize() throws Exception {
    }

    @Test
    public void handleLoginButtonClick() throws Exception {
    }

    @Test
    public void incrementScrollFade() throws Exception {
    }

    @Test
    public void decrementScrollFade() throws Exception {
    }

}