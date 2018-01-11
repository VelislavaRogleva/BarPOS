package app.cores;

import app.entities.User;
import app.enums.Pathable;
import app.spring.config.SpringFXMLLoader;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class StageManager {

    private static final String APP_TITLE = "BarPOS";

    private Stage primaryStage;
    private User user;
    private List<?> searchResults;
//    private PassKeyVerificationService passKeyVerification;
    private SpringFXMLLoader springFXMLLoader;


    public StageManager(SpringFXMLLoader springFXMLLoader, Stage primaryStage) {
        this.springFXMLLoader = springFXMLLoader;
        this.primaryStage = primaryStage;
        this.searchResults = new ArrayList<>();
    }

    public void switchScene(Pathable currentView){
        Parent rootNode = getCurrentNode(currentView.getViewPath());
        show(rootNode, currentView.getViewPath());
    }

    public Parent getPane (Pathable panePath){
        return getCurrentNode(panePath.getViewPath());
    }

    public <S> Parent getPane (Pathable panePath, S controller){
        return getCurrentNode(panePath.getViewPath());
    }


    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<?> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<?> searchResults) {
        this.searchResults = searchResults;
    }

    public <T> T getController(){
        return this.springFXMLLoader.getController();
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
