package app.services.password_service.passkey_rules;

import app.services.password_service.PassKeyRule;

public class PassKeyMaxDigitNumber implements PassKeyRule {

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
