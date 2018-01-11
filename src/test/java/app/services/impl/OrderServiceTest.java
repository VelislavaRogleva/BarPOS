package app.services.impl;


import app.dtos.OrderDto;
import app.entities.BarTable;
import app.entities.Product;
import app.entities.User;

import app.enums.OrderStatus;
import app.services.api.BarTableService;
import app.services.api.OrderService;
import app.services.api.ProductService;
import app.services.api.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BarTableService barTableService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;


    @Test
    public void findOpenOrder() {
        OrderDto order = this.orderService.findOpenOrderByTable(1L);

        Assert.assertFalse("Bar table should be unavailable", order.getBarTable().getAvailable());
        Assert.assertEquals("Order status should be OPEN", OrderStatus.OPEN, order.getStatus());
        Assert.assertEquals("Wrong products size", 2, order.getProducts().size());

        Map<Product, Integer> products = order.getProducts();
        for (Product product : products.keySet()) {
            System.out.println(product + " -> " + products.get(product));
        }

    }



    @Test
    public void testCancelOrder() {
        this.orderService.cancelOrder(1L);
    }

    @Test
    public void testCloseOrder() {
        this.orderService.closeOrder(4L);
    }
}