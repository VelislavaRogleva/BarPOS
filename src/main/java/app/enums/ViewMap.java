package app.enums;

import java.util.ResourceBundle;

public enum ViewMap {

    LOGIN("src/main/resources/views/Login.fxml"),
    TABLE("src/main/resources/views/Table.fxml");

    private String viewPath;

    ViewMap(String viewPath) {
        this.viewPath = viewPath;
    }

    public String getViewPath() {
        return this.viewPath;
    }

    //    LOGIN {
//        @Override
//        String getTitle() {
//            return getFromResourceBundle("loginView.title");
//        }
//
//        @Override
//        String getFXmlFile() {
//            return getFromResourceBundle("/views/Login.fxml");
//        }
//    };
//
//  abstract String getTitle();
//    abstract String getFXmlFile();
//
//    String getFromResourceBundle(String text){
//        return ResourceBundle.getBundle("Bundle").getString(text);
//    }


}
