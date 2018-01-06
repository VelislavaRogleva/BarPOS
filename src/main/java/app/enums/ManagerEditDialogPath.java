package app.enums;

public enum ManagerEditDialogPath implements Pathable {

    MANAGE_PRODUCT_EDIT_DIALOG("src/main/resources/views/view_elements/ManageProductEditDialog.fxml"),
    MANAGE_USER_EDIT_DIALOG("src/main/resources/views/view_elements/ManageUserEditDialog.fxml"),
    MANAGE_CATEGORY_EDIT_DIALOG("src/main/resources/views/view_elements/ManageCategoryEditDialog.fxml");

    private String viewPath;

    ManagerEditDialogPath(String viewPath) {
        this.viewPath = viewPath;
    }

    @Override
    public String getViewPath() {
        return this.viewPath;
    }
}
