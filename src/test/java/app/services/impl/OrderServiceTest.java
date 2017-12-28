package app.services.impl;

import app.entities.BarTable;
import app.entities.Order;
import app.entities.User;
import app.services.api.BarTableService;
import app.services.api.OrderService;
import app.services.api.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BarTableService barTableService;

    @Autowired
    private UserService userService;


    @Test
    public void findOpenOrder() {
        Order order = this.orderService.findOpenOrderByTable(1L);

        System.out.println(order.getStatus());
        System.out.println(order.getUser().getName());
        System.out.println(order.getBarTable().getNumber());
        System.out.println(order.getBarTable().getAvailable());
    }
}
