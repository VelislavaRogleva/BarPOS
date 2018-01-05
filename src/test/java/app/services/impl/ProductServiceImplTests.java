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
    public void getAllProductsFromCategory() {
        Category category = categoryService.getCategoryByName("beers");
        List<Product> productsByCategory = this.productService.getProductsByCategory(category);
        Assert.assertEquals(1, productsByCategory.size());
        Assert.assertEquals("Zagorka", productsByCategory.get(0).getName());
    }

    @Test
    public void testRemoveProduct() {
        Product product = this.productService.getProductByName("Zagorka");
        this.productService.removeProduct(product);

        Product unavailableProduct = this.productService.getProductByName("Zagorka");
        Assert.assertFalse("Product should be unavailable", unavailableProduct.getAvailable());
    }

}