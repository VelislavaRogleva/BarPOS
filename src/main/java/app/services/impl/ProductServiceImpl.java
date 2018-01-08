package app.services.impl;

import app.entities.Category;
import app.entities.Product;
import app.repositories.ProductRepository;
import app.services.api.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //TODO remove if not using
    @Override
    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    @Override
    public List<Product> getAllProductsDesc() {
        return this.productRepository.findAllByOrderByAvailableDesc();
    }

    @Override
    public List<Product> getAllAvailableProducts() { return this.productRepository.findAllByAvailable(true);}

    @Override
    public List<Product> getProductsByCategory(Category category) {
        return this.productRepository.findAllByCategory(category);
    }

    @Override
    public List<Product> getAllAvailableProductsInCategory(Category category) {
        return this.productRepository.findAllByAvailableAndCategory(true, category);
    }

    @Override
    public Product getProductById(Long id) {
        return this.productRepository.findById(id);
    }

    @Override
    public Product getProductByName(String name) {
        return this.productRepository.findByName(name);
    }

    @Override
    public void save(Product product) {
        this.productRepository.save(product);
    }

    @Override
    public void removeProduct(Product product) {
        product.setAvailable(false);
        this.productRepository.save(product);
    }

}