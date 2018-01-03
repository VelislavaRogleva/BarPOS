package app.services.impl;

import app.dtos.OrderDto;
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

import javax.transaction.Transactional;
import java.util.HashMap;
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


    @Test
    public void findOpenOrder() {
        Order order = this.orderService.findOpenOrderByTable(1L);

        System.out.println(order.getStatus());
        System.out.println(order.getUser().getName());
        System.out.println(order.getBarTable().getNumber());
        System.out.println(order.getBarTable().getAvailable());
    }

    @Test
    public void createNewOrder() {
        OrderDto orderDto = new OrderDto();
        BarTable barTable = this.barTableService.getAllBarTables().get(0);
        orderDto.setBarTable(barTable);
        User user = this.userService.getAllRegisteredUsers().get(0);
        orderDto.setUser(user);

        orderDto.addProduct(1L);
        orderDto.increaseQuantity(1L);
        orderDto.addProduct(2L);


        this.orderService.createNewOrder(orderDto);

    }

    @Test
    public void testCancelOrder() {
        this.orderService.cancelOrder(2L);
    }

    @Test
    public void testCloseOrder() {
        this.orderService.closeOrder(3L);
    }
}