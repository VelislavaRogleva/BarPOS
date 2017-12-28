package app.services.api;

import app.entities.Category;

import java.util.List;

public interface CategoryService {
    void save(Category category);

    Category getCategoryByName(String name);

    List<Category> getAllCategories();
}