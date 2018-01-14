package app.services.passkey.rules;

import app.services.api.StatisticService;

public class PassKeyOnlyDigits implements StatisticService.PassKeyRule {

    private static final String ERROR_PASSKEY_NOT_DIGITS = "passkey must contains only digits";

    @Override
    public boolean checkPassKey(String passkey) {
        if (!passkey.matches("\\d+")){
            throw new IllegalArgumentException(ERROR_PASSKEY_NOT_DIGITS);
        }
        return true;
    }
}
