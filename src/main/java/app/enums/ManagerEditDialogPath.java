package app.enums;

public enum ManagerEditDialogPath implements Pathable {

    MANAGE_PRODUCT_EDIT_DIALOG("src/main/resources/views/view_elements/ManageProductEditDialog.fxml"),
    MANAGE_USER_EDIT_DIALOG("src/main/resources/views/view_elements/ManageUserEditDialog.fxml"),
    MANAGE_CATEGORY_EDIT_DIALOG("src/main/resources/views/view_elements/ManageCategoryEditDialog.fxml"),
    MANAGE_CATEGORY_DELETE_DIALOG("src/main/resources/views/view_elements/ManageCategoryDeleteDialog.fxml"),
    MANAGE_PRODUCT_DELETE_DIALOG("src/main/resources/views/view_elements/ManageProductDeleteDialog.fxml");

    private String viewPath;

    ManagerEditDialogPath(String viewPath) {
        this.viewPath = viewPath;
    }

    @Override
    public String getViewPath() {
        return this.viewPath;
    }
}
