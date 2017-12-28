package app.services.api;

import app.entities.User;

import java.util.List;

public interface UserService {
    User save(User user);
    List<User> getAllRegisteredUsers();
}