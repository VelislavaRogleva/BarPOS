package app.services.api;


public interface PassKeyVerificationService {
    String validatePassKey(String passkey);

    String hashPassKey(String plainTextPassKey);

    boolean checkPassKey(String plainTextPassKey, String storedHash) throws RuntimeException;
}
