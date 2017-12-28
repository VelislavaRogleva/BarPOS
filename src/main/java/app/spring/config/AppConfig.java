package app.spring.config;

import app.dev.StageManager;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;

@Configuration
public class AppConfig {

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @Bean
    @Lazy(value = true)
    public StageManager stageManager(Stage stage) throws IOException {
        return new StageManager(springFXMLLoader,stage);
    }

}
