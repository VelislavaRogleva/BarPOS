package app.controllers.sale;

import app.controllers.FxmlController;
import app.entities.Product;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Component;

@Component
public class DescriptionViewController implements FxmlController {

    private final String PRODUCT_IMAGE_DIR_NAME = "static_data/images/products_images/";
    private final String DEFAULT_IMAGE = "static_data/images/products_images/default.png";

    @FXML
    private ImageView descriptionProductImage;
    @FXML
    private Label descriptionProductDescription, descriptionProductNamePrice;

    private Product product;

    public DescriptionViewController() {
    }

    @Override
    public void initialize() {
    }

    public void setProduct(Product product) {
        this.product = product;
        String description;
        if (this.product.getDescription() == null){
            description = "";
        }
        else {
            description = this.product.getDescription();
        }
        this.descriptionProductDescription.setText(description);
        this.descriptionProductNamePrice.setText(String.format("%s | $%.2f",
                this.product.getName(), this.product.getPrice()));

        Image image;
        if (this.product.getImagePath() == null || this.product.getImagePath().isEmpty()) {
            image = new Image(DEFAULT_IMAGE);
        }
        else {
            try {
                image = new Image(PRODUCT_IMAGE_DIR_NAME + this.product.getImagePath());
            }
            catch (IllegalArgumentException e) {
                image = new Image(DEFAULT_IMAGE);
            }
        }
        this.descriptionProductImage.setImage(image);
    }
}