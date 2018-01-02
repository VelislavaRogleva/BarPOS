package app.enums;

public enum ErrorMessages {

    USER_NOT_FOUND("user not found"),
    PASS_KEY_DO_NOT_MATCH("passkey do not match"),
    SELECT_USER("please select user"),
    SYSTEM_ERROR("system error"),
    PASSKEY_RULES_NOT_EXISTENT("passkey_rules directory is renamed or not existent"),
    PASSKEY_RULES_NOT_FOUND("passkey rules not found"),
    INVALID_DB_HASH("the hash stored in the DB is invalid");


    private String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return message;
    }
}
