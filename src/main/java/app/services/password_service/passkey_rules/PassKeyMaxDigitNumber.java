package app.services.password_service.passkey_rules;

import app.services.password_service.PassKeyRule;

public class PassKeyMaxDigitNumber implements PassKeyRule {

    private static final String ERROR_PASSWORD_EMPTY = "Passkey must not exceed 12 digits";

    @Override
    public boolean checkPassword(String password) {
        if (password.length() > 12){
            throw new IllegalArgumentException(ERROR_PASSWORD_EMPTY);
        }
        return true;
    }
}
