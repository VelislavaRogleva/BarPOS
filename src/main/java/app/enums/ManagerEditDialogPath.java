package app.enums;

public enum ManagerEditDialogPath implements Pathable {


    //TODO will be removed

    MANAGE_PRODUCT_EDIT_DIALOG("src/main/resources/views/view_elements/manage_products/ManageProductEditDialog.fxml"),
    MANAGE_PRODUCT_DELETE_DIALOG("src/main/resources/views/view_elements/manage_products/ManageProductDeleteDialog.fxml"),
    MANAGE_CATEGORY_EDIT_DIALOG("src/main/resources/views/view_elements/manage_categories/ManageCategoryEditDialog.fxml"),
    MANAGE_CATEGORY_DELETE_DIALOG("src/main/resources/views/view_elements/manage_categories/ManageCategoryDeleteDialog.fxml"),
    MANAGE_USER_EDIT_DIALOG("src/main/resources/views/view_elements/manage_users/ManageUserEditDialog.fxml"),
    MANAGE_USER_DELETE_DIALOG("src/main/resources/views/view_elements/manage_users/ManageUserDeleteDialog.fxml");

    private String viewPath;

    ManagerEditDialogPath(String viewPath) {
        this.viewPath = viewPath;
    }

    @Override
    public String getViewPath() {
        return this.viewPath;
    }
}
