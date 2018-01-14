package app.services.passkey.rules;

import app.services.api.StatisticService;

public class PassKeyMaxDigitNumber implements StatisticService.PassKeyRule {

    private static final int MAX_NUMBER_OF_DIGITS = 12;
    private static final String ERROR_PASSKEY_TOO_LONG = "passkey must not exceed %d digits";

    @Override
    public boolean checkPassKey(String passkey) {
        if (passkey.length() > MAX_NUMBER_OF_DIGITS){
            throw new IllegalArgumentException(String.format(ERROR_PASSKEY_TOO_LONG, MAX_NUMBER_OF_DIGITS));
        }
        return true;
    }
}
