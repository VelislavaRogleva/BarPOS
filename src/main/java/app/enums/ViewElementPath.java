package app.enums;

public enum ViewElementPath implements Pathable {

    MANAGE_SALE("src/main/resources/views/view_elements/ManageSale.fxml"),
    MANAGE_EMPLOYEE("src/main/resources/views/view_elements/ManageEmployee.fxml"),
    MANAGE_PRODUCT("src/main/resources/views/view_elements/ManageProduct.fxml"),
    MANAGE_CATEGORY("src/main/resources/views/view_elements/ManageCategory.fxml");

    private String viewPath;

    ViewElementPath(String viewPath) {
        this.viewPath = viewPath;
    }

    @Override
    public String getViewPath() {
        return this.viewPath;
    }
}
