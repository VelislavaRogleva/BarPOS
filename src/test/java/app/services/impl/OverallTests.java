package app.services.impl;

import app.dtos.OrderImportDto;
import app.entities.*;
import app.services.api.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OverallTests {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BarTableService barTableService;

    @Test
    public void createDbEntries() {

        User user = new User();
        user.setName("Anton");
        user.setPasswordHash("$2a$10$bjNBEn8NGtyUdVGW060bLeQ27TeRfWB.j6bEVVL6b9vYQbZrSE2G.");
        this.userService.save(user);

        User user2 = new User();
        user2.setName("Plamen");
        user2.setPasswordHash("$2a$10$bjNBEn8NGtyUdVGW060bLeQ27TeRfWB.j6bEVVL6b9vYQbZrSE2G.");
        this.userService.save(user2);

        Category category = new Category();
        category.setName("Pizzas");
        this.categoryService.save(category);

        Product product = new Product();
        product.setName("Marinara");
        product.setCategory(category);
        product.setPrice(5.68);
        product.setAvailable(true);
        product.setBarcode("88896f56");
        product.setDescription("Pretty nice pizza");
        this.productService.save(product);

        Product product2 = new Product();
        product2.setName("Margherita");
        product2.setCategory(category);
        product2.setPrice(12.77);
        product2.setAvailable(true);
        product2.setBarcode("845445623");
        product2.setDescription("Pretty nice pizza");
        this.productService.save(product2);

    }




}