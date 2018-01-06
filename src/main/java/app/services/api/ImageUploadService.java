package app.services.api;

import javafx.scene.control.Button;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import java.io.File;

public interface ImageUploadService {

    File addFileChooser(Stage stage);

    Boolean uploadFile(File sourceFile);

    String getFileExtension(File sourceFile);
}
