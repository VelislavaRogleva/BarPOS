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
    public void createNewOrder() {
        OrderDto orderDto = new OrderDto();
        BarTable barTable = this.barTableService.getAllBarTables().get(0);
        orderDto.setBarTable(barTable);
        User user = this.userService.getAllRegisteredUsers().get(0);
        orderDto.setUser(user);
        Product product1 = this.productService.getProductById(2L);
        orderDto.addProduct(product1.getName());
        orderDto.increaseQuantity(product1.getName());
        Product product2 = this.productService.getProductById(3L);
        orderDto.addProduct(product2.getName());

        this.orderService.createOrUpdateOrder(orderDto);

    }

    @Test
    public void findOpenOrder() {
        OrderDto order = this.orderService.findOpenOrderByTable(1L);

        Assert.assertFalse("Bar table should be unavailable", order.getBarTable().getAvailable());
        Assert.assertEquals("Order status should be OPEN", OrderStatus.OPEN, order.getStatus());
        Assert.assertEquals("Wrong products size", 2, order.getProducts().size());

        Map<String, Integer> products = order.getProducts();
        for (String product : products.keySet()) {
            System.out.println(product + " -> " + products.get(product));
        }

    }

    @Test
    public void updateOrderTest() {
        OrderDto orderDto = this.orderService.findOpenOrderByTable(1L);
        Product product1 = this.productService.getProductById(2L);
        Product product2 = this.productService.getProductById(3L);
        Product product3 = this.productService.getProductById(4L);

        orderDto.increaseQuantity(product1.getName());
        orderDto.addProduct(product3.getName());
        orderDto.decreaseQuantity(product2.getName());
        this.orderService.createOrUpdateOrder(orderDto);

        OrderDto order = this.orderService.findOpenOrderByTable(1L);

        Assert.assertEquals("Wrong products size", 3, order.getProducts().size());
        Assert.assertEquals("Wrong productQuantity after increase", 4, (int) order.getProducts().get("Aftershock"));
        Assert.assertEquals("Wrong product quantity after decrease", 0, (int) order.getProducts().get("Red wine"));


    }


    @Test
    public void testCancelOrder() {
        this.orderService.cancelOrder(1L);
    }

    @Test
    public void testCloseOrder() {
        this.orderService.closeOrder(2L);
    }
}