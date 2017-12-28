package app.services.password_service.passkey_rules;

import app.services.password_service.PassKeyRule;

public class PassKeyEmpty implements PassKeyRule {

    private static final String ERROR_PASSKEY_EMPTY = "passkey must not be empty";

    @Override
    public boolean checkPassKey(String passkey) {
        if (passkey.isEmpty()){
            throw new IllegalArgumentException(ERROR_PASSKEY_EMPTY);
        }
        return true;
    }
}

