package app.services.impl;

import app.entities.User;
import app.repositories.UserRepository;
import app.services.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllRegisteredUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getAllActiveUsers() {
        return this.userRepository.findAllActiveUsers();
    }

    //TODO removeUser

}
