package app.services.impl;

import app.entities.Category;
import app.entities.Product;
import app.entities.User;
import app.repositories.ProductRepository;
import app.services.api.SearchService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SearchServiceImplTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Transactional
    public void findProductsByNameShouldReturnCorrectValue() {
        String text = "d w";
        List<Product> productList = this.searchService.findProductsByName(text);
        String expected = "Red wine";
        String actual = productList.get(0).getName();
        System.out.println(productList.get(0).getName());
        System.out.println(productList.get(0).getPrice());
        Assert.assertEquals("Names Does not mactch", actual, expected);
    }

    @Test
    @Transactional
    public void findProductsByNameShouldReturnCorrectCount() {
        int expected = 2;
        String text = "a";
        List<Product> productList = this.searchService.findProductsByName(text);
        int actual = productList.size();
        productList.stream().forEach(x -> System.out.println(x.getName()));
        Assert.assertEquals("Count does not match", actual, expected);
    }

    @Test
    @Transactional
    public void findCategoriesByName() {
        String text = "co";
        String expected = "alcohol";
        List<Category> categoryList = this.searchService.findCategoriesByName(text);
        String actual = categoryList.get(0).getName();
        categoryList.stream().forEach(x -> System.out.println(x.getName()));
        Assert.assertEquals("Category does not match", actual, expected);
    }

    @Test
    @Transactional
    public void findUsersByName() {
        String text = "an";
        List<User> userList = this.searchService.findUsersByName(text);
        String actual = userList.get(0).getName();
        String expected = "Ivan";
        userList.stream().forEach(x -> System.out.println(x.getName()));
        Assert.assertEquals("Usernames does not match !", actual, expected);

    }
}