package app;

import app.dev.StageManager;
import app.enums.ViewMap;
import app.factory.SceneFactory;
import de.felixroske.jfxsupport.FXMLView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main extends Application {
	private final static String currentDir = System.getProperty("user.dir");

	protected ConfigurableApplicationContext springContext;
	protected StageManager stageManager;


	public static void main(String[] args) {
		Application.launch(Main.class, args);
	}

	@Override
	public void init() throws Exception {
		springContext = bootstrapSpringApplicationContext();
//		this.springContext = SpringApplication.run(Main.class);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stageManager = springContext.getBean(StageManager.class, stage);
		displayInitialScene();

//		SceneFactory.setStage(stage);
//		SceneFactory.showScene("Login");
	}

	@Override
	public void stop() throws Exception {
		springContext.stop();
	}

	protected void displayInitialScene() {
		stageManager.switchScene(ViewMap.LOGIN);
	}

	private ConfigurableApplicationContext bootstrapSpringApplicationContext() {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(Main.class);
		String[] args = getParameters().getRaw().stream().toArray(String[]::new);
		return builder.run(args);
	}

}
