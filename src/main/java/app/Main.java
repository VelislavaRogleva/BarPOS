package app;

import app.factory.SceneFactory;
import javafx.application.Application;
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
		this.springContext = SpringApplication.run(Main.class);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		SceneFactory.setStage(primaryStage);
		SceneFactory.showScene("LoginView");
	}

	@Override
	public void stop() throws Exception {
		this.springContext.stop();
	}


	public static void main(String[] args) {
//		System.out.println(currentDir);
		launch(Main.class, args);
	}
}
