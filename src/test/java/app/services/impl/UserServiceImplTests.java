package app.services.impl;

import app.entities.User;
import app.services.api.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTests {

    private final int registeredUsersSize = 3;
    private final String firstRegisteredUser = "Ivan";
    private List<User> allRegisteredUsers;

    @Autowired
    private UserService userService;

    @Before
    public void initialize() {
        allRegisteredUsers = userService.getAllRegisteredUsers();
    }

    @Test
    public void testGetAllRegisteredUsersBySize() {
        Assert.assertEquals("Wrong size of registered users",
                allRegisteredUsers.size(), registeredUsersSize);
    }

    @Test
    public void testFirstRegisteredUser() {
        Assert.assertEquals("Wrong name of first registered user",
                allRegisteredUsers.get(0).getName(), firstRegisteredUser);
    }
}