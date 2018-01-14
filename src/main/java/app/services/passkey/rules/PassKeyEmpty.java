package app.services.passkey.rules;

import app.services.api.StatisticService;

public class PassKeyEmpty implements StatisticService.PassKeyRule {

    private static final String ERROR_PASSKEY_EMPTY = "passkey must not be empty";

    @Override
    public boolean checkPassKey(String passkey) {
        if (passkey.isEmpty()){
            throw new IllegalArgumentException(ERROR_PASSKEY_EMPTY);
        }
        return true;
    }
}

