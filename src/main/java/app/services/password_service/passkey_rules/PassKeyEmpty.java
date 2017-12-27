package app.services.password_service.passkey_rules;

import app.services.password_service.PassKeyRule;

public class PassKeyEmpty implements PassKeyRule {

    private static final String ERROR_PASSWORD_EMPTY = "Passkey must not be empty";

    @Override
    public boolean checkPassword(String password) {
        if (password.isEmpty()){
            throw new IllegalArgumentException(ERROR_PASSWORD_EMPTY);
        }
        return true;
    }
}
