package app.services.impl;

import app.services.api.ImageUploadService;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;

import javafx.scene.control.Label;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {


    private static final int IMG_WIDTH = 300;
    private static final int IMG_HEIGHT = 160;
    private static final String PRODUCT_IMG_DIR_NAME = "src\\main\\resources\\static_data\\images\\products_images\\";
    private static final String PRODUCT_IMG_DIR_NAME_RESOURCE = "target\\classes\\static_data\\images\\products_images\\";
    private static final String[] JPG_FILTER = {"JPG files(*.jpg)","*.JPG" };
    private static final String[] PNG_FILTER = {"PNG files(*.png)","*.PNG"};
    private static final String[] GIF_FILTER = {"GIF files(*.gif)", "*.GIF"};
    private static final String[] BMP_FILTER = {"BMP files(*.bmp)", "*.BMP"};
    private static final String PROCESSING_ERROR_TITLE = "Image processing error";
    private static final String PROCESSING_ERROR_HEADER = "Image cannot be copied into the new location!";
    private static final String EXIST_ERROR_TITLE = "Image exists";
    private static final String EXIST_ERROR_HEADER = "Image already exists.\r\nDo you want to replace currently existing image?";
    private static final double THRESHOLD = 1.0;
    private static final int X_POS = 0;
    private static final int Y_POS = 0;

    /*
    choosing file from storage
     */
    @Override
    public File addFileChooser(Stage stage){
        final File[] sourceFile = new File[1];
            final FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter ext1 = new FileChooser.ExtensionFilter(JPG_FILTER[0], JPG_FILTER[1]);
            FileChooser.ExtensionFilter ext2 = new FileChooser.ExtensionFilter(PNG_FILTER[0],PNG_FILTER[1]);
            FileChooser.ExtensionFilter ext3 = new FileChooser.ExtensionFilter(GIF_FILTER[0],GIF_FILTER[1]);
            FileChooser.ExtensionFilter ext4 = new FileChooser.ExtensionFilter(BMP_FILTER[0],BMP_FILTER[1]);
            fileChooser.getExtensionFilters().addAll(ext1,ext2,ext3,ext4 );

            sourceFile[0] = fileChooser.showOpenDialog(stage);
        return  sourceFile[0];
    }

    @Override
    public Boolean uploadFile(File sourceFile){
        if (null != sourceFile){
            try {
                String destination = PRODUCT_IMG_DIR_NAME + sourceFile.getName();
                String destinationTarget = PRODUCT_IMG_DIR_NAME_RESOURCE + sourceFile.getName();
                File targetFile = new File(destination);
                File fileTarget = new File(destinationTarget);
                boolean isNewImage = isFileReplace(targetFile);
                if (isNewImage){
                    BufferedImage resultImage = resizeImage(sourceFile);
                    String extension = getFileExtension(sourceFile);
                   ImageIO.write(resultImage, extension, fileTarget);
                   return ImageIO.write(resultImage, extension, targetFile);
                } else {
                    return true;
                }
            } catch (IOException ioe) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(PROCESSING_ERROR_TITLE);
                alert.setHeaderText(PROCESSING_ERROR_HEADER);
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
            alert.setTitle(EXIST_ERROR_TITLE);
            alert.setHeaderText(EXIST_ERROR_HEADER);
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
        graphics.clearRect(X_POS, Y_POS, IMG_WIDTH, IMG_HEIGHT);
        graphics.drawImage(sourceImage, X_POS, Y_POS, IMG_WIDTH, IMG_HEIGHT, null);
        graphics.dispose();


        return resultImage;
    }


    private BufferedImage scaleImage(File sourceFile) throws IOException {

            BufferedImage sourceImage = ImageIO.read(sourceFile);

            double sourceWidth = (double) sourceImage.getWidth();
            double sourceHeight = (double) sourceImage.getHeight();


            double scale = Math.min(THRESHOLD,  Math.min((IMG_WIDTH / sourceWidth), (IMG_HEIGHT / sourceHeight)));
            double scaleWidth = scale * sourceWidth;
            double scaleHeight = scale * sourceHeight;
            double maxAspect = ((double)IMG_WIDTH) / IMG_HEIGHT;
            double aspect = sourceWidth / sourceHeight;
            double picWidth = 0.0;
            double picHeight = 0.0;
            if (maxAspect <= aspect && sourceWidth > ((double)IMG_WIDTH)){
                picWidth = IMG_WIDTH;
                picHeight = Math.min(( (double) IMG_HEIGHT) , ((double) IMG_WIDTH) / aspect);
            } else if (maxAspect > aspect && sourceHeight > ((double)IMG_HEIGHT) ){

                picWidth = Math.min(((double)IMG_WIDTH) , ((double)IMG_HEIGHT) / aspect);
                picHeight = IMG_HEIGHT;
            } else {
                picWidth = sourceWidth;
                picHeight = sourceHeight;
            }

            //TYPE_INT_RGB - 4 bytes per pixel, without alpha channel
            BufferedImage resultImage = new BufferedImage((int) picWidth, (int) picHeight, BufferedImage.TYPE_INT_RGB);

            //save image quality after resizing
            Graphics2D graphics = resultImage.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.clearRect(X_POS, Y_POS, (int) picWidth, (int) picHeight);
            graphics.drawImage(sourceImage, X_POS, Y_POS, (int) picWidth, (int) picHeight, null);
            graphics.dispose();


        return resultImage;
    }

}
