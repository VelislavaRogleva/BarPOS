package app.services.impl;

import app.entities.Category;
import app.services.api.CategoryService;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CateogryServiceTests {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void testAddCategory() {
        Category category = new Category();
        category.setName("Beer");
        this.categoryService.save(category);
    }

    @Test
    public void getAllCategories() {
        Category category = new Category();
        category.setName("Dessert");
        this.categoryService.save(category);

        List<Category> categories = this.categoryService.getAllCategories();
        Assert.assertEquals(2, categories.size());
        Assert.assertEquals("Dessert", categories.get(1).getName());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void throwWhenAddingCategoryWithSameName() {
        Category category = new Category();
        category.setName("Dessert");
        this.categoryService.save(category);
    }
}
