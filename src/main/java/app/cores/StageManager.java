package app.cores;

import app.entities.User;
import app.enums.Pathable;
import app.spring.config.SpringFXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class StageManager {

    private static final String APP_TITLE = "BarPOS";

    private Stage primaryStage;
    private User user;
//    private PassKeyVerificationService passKeyVerification;
    private SpringFXMLLoader springFXMLLoader;


    public StageManager(SpringFXMLLoader springFXMLLoader, Stage primaryStage) {
        this.springFXMLLoader = springFXMLLoader;
        this.primaryStage = primaryStage;
    }

    public void switchScene(Pathable currentView){
        Parent rootNode = getCurrentNode(currentView.getViewPath());
        show(rootNode, currentView.getViewPath());
    }

    public Parent getPane (Pathable panePath){
        Parent rootNode = getCurrentNode(panePath.getViewPath());
        return rootNode;
    }

    public <S> Parent getPane (Pathable panePath, S controller){
        Parent rootNode = getCurrentNode(panePath.getViewPath());
        return rootNode;
    }


    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public <T> T getController(){
        return this.springFXMLLoader.getController();
    }


//TODO if using password to log in manager
//    public PassKeyVerificationService getPassKeyVerification() {
//        return this.passKeyVerification;
//    }
//
//    @Autowired
//    public void setPassKeyVerification(PassKeyVerificationService passKeyVerification) {
//        this.passKeyVerification = passKeyVerification;
//    }

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
