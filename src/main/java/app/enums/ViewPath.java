package app.enums;

public enum ViewPath implements Pathable {

    LOGIN("src/main/resources/views/Login.fxml"),
    TABLE("src/main/resources/views/Table.fxml"),
    MANAGER("src/main/resources/views/Manager.fxml");

    private String viewPath;

    ViewPath(String viewPath) {
        this.viewPath = viewPath;
    }

    @Override
    public String getViewPath() {
        return this.viewPath;
    }
}
