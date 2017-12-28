package app.services.impl;

import app.entities.Category;
import app.entities.Product;
import app.services.api.CategoryService;
import app.services.api.ProductService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceImplTests {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Test
    public void testAddingNewProduct() {
        Product product = new Product();
        product.setName("Tuborg");
        product.setDescription("Some kind of beer");
        product.setAvailable(true);
        product.setBarcode("48887dd6");
        product.setPrice(2.88);

        Category category = categoryService.getCategoryByName("Beer");
        product.setCategory(category);

        this.productService.save(product);
    }

    @Test
    public void getAllProductsFromCategory() {
        Category category = categoryService.getCategoryByName("Beer");
        List<Product> productsByCategory = this.productService.getProductsByCategory(category);
        Assert.assertEquals(1, productsByCategory.size());
        Assert.assertEquals("Tuborg", productsByCategory.get(0).getName());
    }


}