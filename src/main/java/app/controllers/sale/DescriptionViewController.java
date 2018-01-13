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

    private final String PRODUCT_IMAGE_DIR_NAME = "src\\main\\resources\\static_data\\images\\products_images\\";

    @FXML
    private ImageView descriptionProductImage;
    @FXML
    private Label descriptionProductDescription, descriptionProductNamePrice;

    private Product product;

    @Override
    public void initialize() {
        this.descriptionProductDescription.setText(this.product.getDescription());
        this.descriptionProductNamePrice.setText(String.format("%s | $%.2f",
                this.product.getName(), this.product.getPrice()));

        if (this.product.getImagePath() == null || this.product.getImagePath().isEmpty()) {
            //TODO Set default image
        }
        else {
            Image image = new Image(PRODUCT_IMAGE_DIR_NAME + this.product.getImagePath());
            this.descriptionProductImage.setImage(image);
        }
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
