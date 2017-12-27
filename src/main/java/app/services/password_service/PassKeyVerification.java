package app.services.password_service;

import java.util.List;

public interface PassKeyVerification {
    String validatePassword(String password);

    String hashPassword(String plainTextPassword);

    boolean checkPassword(String plainTextPassword, String storedHash);
}
