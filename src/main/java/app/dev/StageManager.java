package app.dev;

import app.enums.ViewMap;
import app.spring.config.SpringFXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;


public class StageManager {

    private static final String APP_TITLE = "BarPOS";

    private Stage primaryStage;
    private SpringFXMLLoader springFXMLLoader;


    public StageManager(SpringFXMLLoader springFXMLLoader, Stage primaryStage) {
        this.springFXMLLoader = springFXMLLoader;
        this.primaryStage = primaryStage;
    }

    public void switchScene(ViewMap currentView){
        Parent rootNode = getCurrentNode(currentView.getViewPath());
        show(rootNode, currentView.getViewPath());
    }

    private void show(Parent rootNode, String filePath) {

        Scene scene = prepareScene(rootNode);
        String title = extractTitle(filePath);
        primaryStage.setResizable(false);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private String extractTitle(String filePath) {
        int lastSlash = filePath.lastIndexOf("/");
        String title = filePath.substring(lastSlash+1).replace(".fxml","");
        return String.format("%s - %s",APP_TITLE,title);
    }

    private Scene prepareScene(Parent rootNode){
        Scene scene = primaryStage.getScene();

        if (scene == null) {
            scene = new Scene(rootNode);
        }
        scene.setRoot(rootNode);
        return scene;
    }

    private Parent getCurrentNode(String filePath) {
        Parent rootNode = null;
        try{
            rootNode = springFXMLLoader.load(filePath);
        } catch (Exception e) {
            throw new IllegalArgumentException("Node cannot be loaded");
        }
        if (null == rootNode){
            throw new IllegalArgumentException("Node must not be null");
        }
        return rootNode;

    }


}
