package app.services.password_service.passkey_rules;

import app.services.password_service.PassKeyRule;

public class PassKeyOnlyDigits implements PassKeyRule {

    private static final String ERROR_PASSWORD_EMPTY = "Passkey must contains only digits";

    @Override
    public boolean checkPassword(String password) {
        if (password.matches("\\D+")){
            throw new IllegalArgumentException(ERROR_PASSWORD_EMPTY);
        }
        return true;
    }
}
