package app.services.impl;

import app.entities.Category;
import app.entities.Product;
import app.entities.User;
import app.repositories.CategoryRepository;
import app.repositories.ProductRepository;
import app.repositories.UserRepository;
import app.services.api.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchServiceImpl implements SearchService{

    private UserRepository userRepository;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    public SearchServiceImpl(UserRepository userRepository,
                             ProductRepository productRepository,
                             CategoryRepository categoryRepository){
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Product> findProductsByName(String text) {
        return null;
    }

    @Override
    public List<Category> findCategoriesByName(String text) {
        return null;
    }

    @Override
    public List<User> findUsersByName(String text) {
        return null;
    }
}
