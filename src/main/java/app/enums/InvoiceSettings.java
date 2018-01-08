package app.enums;

public enum InvoiceSettings {

    COMPANY_TITLE("Drunk Inc.\r\n"),
    BAR_ADDRESS("Sofia, “Drunkhard” 19\r\nEIK 123456789\r\n"),
    BAR_FINANCIAL_INFO("Drunkard Bar\r\nSofia “No Memory” 79\r\nZDDS N: BG 123456789"),
    OPERATOR_INFO("Operator: %s\r\n");

    private String setting;

    InvoiceSettings(String viewPath) {
        this.setting = viewPath;
    }

    public String getSetting() {
        return this.setting;
    }
}
