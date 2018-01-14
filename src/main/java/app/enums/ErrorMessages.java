package app.enums;

public enum ErrorMessages {

    USER_NOT_FOUND("user not found"),
    PASS_KEY_DO_NOT_MATCH("passkey do not match"),
    SELECT_USER("please select user"),
    SYSTEM_ERROR("system error"),
    PASSKEY_RULES_NOT_EXISTENT("rules directory is renamed or not existent"),
    PASSKEY_RULES_NOT_FOUND("passkey rules not found"),
    INVALID_DB_HASH("the hash stored in the DB is invalid"),
    NO_TABLE_MODIFICATION_ERROR("Cannot modify table with open order!"),
    NO_OVERRIDE_TABLE("This table exists. No override allowed!"),
    BAD_ACTION("Cannot complete action! Incorrect field value"),
    CATEGORY_NOT_EMPTY("Cannot remove non empty category!"),
    CATEGORY_NAME_TAKEN ("Category name is already taken!"),
    BARCODE_NO_OVERRIDE("This barcode exists. No override allowed!"),
    USER_NO_OVERRIDE("The user exists. No override allowed!");


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
