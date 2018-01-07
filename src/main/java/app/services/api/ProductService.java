package app.services.api;

import app.entities.Category;
import app.entities.Product;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    List<Product> getAllAvailableProducts();

    List<Product> getProductsByCategory(Category category);

    Product getProductById(Long id);

    Product getProductByName(String name);

    void save(Product product);

    void removeProduct(Product product);
}