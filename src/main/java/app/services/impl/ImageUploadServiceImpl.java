package app.services.impl;

import app.services.api.ImageUploadService;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;

import javafx.scene.control.Label;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {


    private static final int IMG_WIDTH = 300;
    private static final int IMG_HEIGHT = 160;
    private static final String PRODUCT_IMG_DIR_NAME = "src\\main\\resources\\static_data\\images\\products_images\\";

    /*
    choosing file from storage
     */
    @Override
    public File addFileChooser(Stage stage){
        final File[] sourceFile = new File[1];
            final FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter ext1 = new FileChooser.ExtensionFilter("JPG files(*.jpg)","*.JPG");
            FileChooser.ExtensionFilter ext2 = new FileChooser.ExtensionFilter("PNG files(*.png)","*.PNG");
            FileChooser.ExtensionFilter ext3 = new FileChooser.ExtensionFilter("GIF files(*.gif)","*.GIF");
            FileChooser.ExtensionFilter ext4 = new FileChooser.ExtensionFilter("BMP files(*.bmp)","*.BMP");
            fileChooser.getExtensionFilters().addAll(ext1,ext2,ext3,ext4 );

            sourceFile[0] = fileChooser.showOpenDialog(stage);
        return  sourceFile[0];
    }

    @Override
    public Boolean uploadFile(File sourceFile){
        if (null != sourceFile){
            try {
                String destination = PRODUCT_IMG_DIR_NAME + sourceFile.getName();
                File targetFile = new File(destination);
                boolean isNewImage = isFileReplace(targetFile);
                if (isNewImage){
                    BufferedImage resultImage = resizeImage(sourceFile);
                    String extension = getFileExtension(sourceFile);
                    return ImageIO.write(resultImage, extension, targetFile);
                } else {
                    return true;
                }
            } catch (IOException ioe) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Image processing error");
                alert.setHeaderText("Image cannot be copied into the new location!");
                alert.showAndWait();
            }
        }
        return false;
    }

    @Override
    public String getFileExtension(File sourceFile){

        String sourceFileName = sourceFile.getName();
        int dotIndex = sourceFileName.lastIndexOf(".");

        return sourceFileName.substring(dotIndex + 1);
    }

    private Boolean isFileReplace(File sourceFile) throws IOException {
        if (!sourceFile.isDirectory() && sourceFile.exists()){

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Image exists");
            alert.setHeaderText("Image already exists.\r\nDo you want to replace currently existing image?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.CANCEL){
                alert.close();
                return false;
            } else {
                alert.close();
                return true;
            }
        }
        return true;
    }


    private BufferedImage resizeImage(File sourceFile) throws IOException {

        BufferedImage sourceImage = ImageIO.read(sourceFile);

        //TYPE_INT_RGB - 4 bytes per pixel, without alpha channel
        BufferedImage resultImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);

        //save image quality after resizing
        Graphics2D graphics = resultImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.clearRect(0, 0, IMG_WIDTH, IMG_HEIGHT);
        graphics.drawImage(sourceImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
        graphics.dispose();

        return resultImage;
    }

}
