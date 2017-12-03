package app;

import app.factory.SceneFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main extends Application {
	private final static String currentDir = System.getProperty("user.dir");

	private ConfigurableApplicationContext springContext;

	@Override
	public void init() throws Exception {
		springContext = SpringApplication.run(Main.class);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		SceneFactory.setStage(primaryStage);
		SceneFactory.showScene("Login");
	}

	@Override
	public void stop() throws Exception {
		springContext.stop();
	}


	public static void main(String[] args) {
		System.out.println(currentDir);
		launch(Main.class, args);
	}
}
