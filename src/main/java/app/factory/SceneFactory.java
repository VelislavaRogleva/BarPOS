package app.factory;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

public class SceneFactory {
    //Name of our app
    private static final String APP_NAME = "BarPOS";

    //Name of current window ex. "<name_of_our_app> - <name_of_current_screen>"
    private static final String TITLE_FORMAT = APP_NAME + " - %s";

    //Relative path to scenes' package
    private static final String SCENES_PATH = "src/main/resources/views/";

    //Main window created by JavaFX
    private static Stage PRIMARY_STAGE;

    //Getting primaryStage for future reusing, this method is ran only once in Main Method
    public static void setStage(Stage primaryStage) {
        PRIMARY_STAGE = primaryStage;
        //PRIMARY_STAGE.setMaximized(true);
        PRIMARY_STAGE.setResizable(false);
        //token scene for the purpose of primaryStage having a scene to set in showScene method
        PRIMARY_STAGE.setScene(new Scene(new Label()));
    }

    //Note: Switch scene or switch node?
    //      Switch node - retaining previous window size
    public static void showScene(String sceneName) {
        try {
            Parent root = FXMLLoader.load(getURL(sceneName));
            PRIMARY_STAGE.setTitle(String.format(TITLE_FORMAT, sceneName));
            PRIMARY_STAGE.getScene().setRoot(root);
            PRIMARY_STAGE.show();
        } catch (IOException e) {
            //Popping exception window for dev purposes
            Stage stage = new Stage();
            stage.setTitle("Exception");
            Label text = new Label(e.getMessage());
            text.setFont(Font.font(20));
            text.setAlignment(Pos.CENTER);
            stage.setScene(new Scene(text));
            stage.setResizable(false);
            stage.setAlwaysOnTop(true);
            stage.show();
        }

    }

    private static URL getURL(String sceneName) throws IOException {
        return Paths.get(SCENES_PATH +
                sceneName + ".fxml").toUri().toURL();
    }
}
