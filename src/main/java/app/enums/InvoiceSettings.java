package app.enums;

public enum InvoiceSettings {

    COMPANY_TITLE("Drunk Inc."),
    BAR_ADDRESS("Sofia, “Drunkhard” 19\r\nEIK 123456789"),
    BAR_FINANCIAL_INFO("Drunkard Bar\r\nSofia “No Memory” 79\r\nZDDS N: BG 123456789"),
    OPERATOR_INFO("Operator: %s"),
    RECEIPT_COUNTRY("BG"),
    RECEIPT_TYPE("FISCAL RECEIPT"),
    TAX_TITLE("Tax"),
    TOTAL_TITLE("Total"),
    HOUR_MINUTES_PATTERN("HH:mm"),
    DATE_PATTERN("dd-MM-yyyy "),
    INVOICE_SERIAL_NUMBER_PATTERN("00000000"),
    PRICE_PATTERN("$%.2f"),
    PRODUCT_TOTAL_TITLE("sum*n"),
    PRODUCT_QUANTITY("%dx");


    private String setting;

    InvoiceSettings(String viewPath) {
        this.setting = viewPath;
    }

    public String getSetting() {
        return this.setting;
    }
}
