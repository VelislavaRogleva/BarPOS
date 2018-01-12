package app.services.api;

import app.entities.Category;
import app.entities.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface ProductService {
    //TODO remove is not using
    List<Product> getAllProducts();

    List<Product> getAllProductsDesc();

    List<Product> getAllAvailableProducts();

    List<Product> getProductsByCategory(Category category);

    List<Product> getAllAvailableProductsInCategory(Category category);

    Product getProductById(Long id);

    Product getProductByName(String name);

    void save(Product product);

    void removeProduct(Product product);

    Map<Long, Product> getAllProductsInOpenOrders();
}