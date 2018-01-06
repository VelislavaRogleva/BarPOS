package app.spring.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;

@Component
public class SpringFXMLLoader {

    private final ApplicationContext context;
    private FXMLLoader fxmlLoader;

    public SpringFXMLLoader(ApplicationContext context) {
        this.context = context;
    }

    public Parent load(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);
        this.fxmlLoader = loader;
        loader.setLocation(getURL(fxmlPath));
        return loader.load();
    }

    public <T> T getController(){
        return this.fxmlLoader.getController();
    }

    private static URL getURL(String stringPath) throws IOException {
        return Paths.get(stringPath).toUri().toURL();
    }
}
