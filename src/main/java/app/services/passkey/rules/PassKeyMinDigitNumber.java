package app.services.passkey.rules;

import app.services.api.StatisticService;

public class PassKeyMinDigitNumber implements StatisticService.PassKeyRule {

    private static final int MIN_NUMBER_OF_DIGITS = 4;
    private static final String ERROR_PASSKEY_TOO_SHORT = "passkey must contains at least %d digits";

    @Override
    public boolean checkPassKey(String passkey) {
        if (passkey.length()<4){
            throw new IllegalArgumentException(String.format(ERROR_PASSKEY_TOO_SHORT, MIN_NUMBER_OF_DIGITS));
        }
        return true;
    }
}
