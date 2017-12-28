package app.services.password_service.passkey_rules;

import app.services.password_service.PassKeyRule;

public class PassKeyOnlyDigits implements PassKeyRule {

    private static final String ERROR_PASSKEY_NOT_DIGITS = "passkey must contains only digits";

    @Override
    public boolean checkPassKey(String passkey) {
        if (passkey.matches("\\D+")){
            throw new IllegalArgumentException(ERROR_PASSKEY_NOT_DIGITS);
        }
        return true;
    }
}
