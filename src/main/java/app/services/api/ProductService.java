package app.services.api;

import app.entities.Category;
import app.entities.Product;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    List<Product> getProductsByCategory(Category category);

    Product getProductDetails(Long id);

    void save(Product product);

    void deleteProduct(Long id);
}
