package app.services.password_service.passkey_rules;

import app.services.password_service.PassKeyRule;

public class PassKeyMinDigitNumber implements PassKeyRule {

    private static final String ERROR_PASSWORD_EMPTY = "Password must contains at least 4 digits";

    @Override
    public boolean checkPassword(String password) {
        if (password.length()<4){
            throw new IllegalArgumentException(ERROR_PASSWORD_EMPTY);
        }
        return true;
    }
}
