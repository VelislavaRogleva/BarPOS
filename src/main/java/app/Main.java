package app;

import app.cores.StageManager;
import app.enums.ViewPath;
import app.services.api.UserService;
import app.services.impl.UserServiceImpl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main extends Application {
	private final static String currentDir = System.getProperty("user.dir");

	protected ConfigurableApplicationContext springContext;
	protected StageManager stageManager;
	protected UserService userService;


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
		setExitProgramRequest(stage);
		stageManager = springContext.getBean(StageManager.class, stage);
		userService = springContext.getBean(UserServiceImpl.class);
		if (this.userService.getAllActiveUsers().size() < 1){
			stageManager.switchScene(ViewPath.MANAGER);
		} else {
			stageManager.switchScene(ViewPath.MANAGER);
		}
	}

	@Override
	public void stop() throws Exception {
		springContext.stop();
	}

	private ConfigurableApplicationContext bootstrapSpringApplicationContext() {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(Main.class);
		String[] args = getParameters().getRaw().stream().toArray(String[]::new);
		return builder.run(args);
	}

	private void setExitProgramRequest(Stage stage) {
		Pane pane = new Pane();
		Label questionLabel = new Label("Are you sure you want to exit?");
		Button yesButton = new Button("Yes");
		Button noButton = new Button("No");
		Stage exitStage = new Stage();

		yesButton.setOnAction(e -> {
			exitStage.close();
			stage.close();
		});

		noButton.setOnAction(e -> {
			exitStage.close();
		});

		pane.getChildren().addAll(questionLabel, yesButton, noButton);
		pane.setPrefSize(300, 200);
		questionLabel.setLayoutY(50);
		questionLabel.setLayoutX(30);
		yesButton.setLayoutX(43);
		yesButton.setLayoutY(130);
		noButton.setLayoutX(182);
		noButton.setLayoutY(130);

		questionLabel.setFont(Font.font(18));

		yesButton.setFont(Font.font(14));
		yesButton.setPrefSize(79, 35);

		noButton.setFont(Font.font(14));
        noButton.setPrefSize(79, 35);

		Scene scene = new Scene(pane, 300, 200);
		exitStage.setResizable(false);
		exitStage.setScene(scene);
		exitStage.initModality(Modality.APPLICATION_MODAL);

		stage.setOnCloseRequest(e -> {
			e.consume();
			exitStage.show();
		});
	}
}
