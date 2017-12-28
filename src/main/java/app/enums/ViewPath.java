package app.enums;

public enum ViewPath {

    LOGIN("src/main/resources/views/Login.fxml"),
    TABLE("src/main/resources/views/Table.fxml");

    private String viewPath;

    ViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

    public String getViewPath() {
        return this.viewPath;
    }
}
