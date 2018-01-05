package app.services.api;

import app.entities.Category;
import app.entities.Product;
import app.entities.User;

import java.util.List;

public interface SearchService {
    List<Product> findProductsByName(String text);

    List<Category> findCategoriesByName(String text);

    List<User> findUsersByName(String text);
}
