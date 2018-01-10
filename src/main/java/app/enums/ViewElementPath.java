package app.enums;

public enum ViewElementPath implements Pathable {

    MANAGE_SALE("src/main/resources/views/view_elements/ManageSale.fxml"),

    /*
     * product's paths
     */
    MANAGE_PRODUCT("src/main/resources/views/view_elements/manage_products/ManageProduct.fxml"),
    MANAGE_PRODUCT_EDIT_DIALOG("src/main/resources/views/view_elements/manage_products/ManageProductEditDialog.fxml"),
    MANAGE_PRODUCT_DELETE_DIALOG("src/main/resources/views/view_elements/manage_products/ManageProductDeleteDialog.fxml"),
    /*
     * category's paths
     */
    MANAGE_CATEGORY("src/main/resources/views/view_elements/manage_categories/ManageCategory.fxml"),
    MANAGE_CATEGORY_EDIT_DIALOG("src/main/resources/views/view_elements/manage_categories/ManageCategoryEditDialog.fxml"),
    MANAGE_CATEGORY_DELETE_DIALOG("src/main/resources/views/view_elements/manage_categories/ManageCategoryDeleteDialog.fxml"),
    /*
     * user's paths
     */
    MANAGE_USER("src/main/resources/views/view_elements/manage_users/ManageUser.fxml"),
    MANAGE_USER_EDIT_DIALOG("src/main/resources/views/view_elements/manage_users/ManageUserEditDialog.fxml"),
    MANAGE_USER_DELETE_DIALOG("src/main/resources/views/view_elements/manage_users/ManageUserDeleteDialog.fxml"),

    /*
     * table's paths
     */
    MANAGE_BARTABLE("src/main/resources/views/view_elements/manage_table/ManageBarTable.fxml"),
    MANAGE_BARTABLE_EDIT_DIALOG("src/main/resources/views/view_elements/manage_table/ManageBarTableEditDialog.fxml"),
    MANAGE_BARTABLE_DELETE_DIALOG("src/main/resources/views/view_elements/manage_table/ManageBarTableDeleteDialog.fxml"),
    /*
     * invoice path
     */
    INVOICE("src/main/resources/views/Invoice.fxml"),

    /*
     * sale's pay view path
     */
    PAY_VIEW("src/main/resources/views/view_elements/PayView.fxml");

    private String viewPath;

    ViewElementPath(String viewPath) {
        this.viewPath = viewPath;
    }

    @Override
    public String getViewPath() {
        return this.viewPath;
    }
}
