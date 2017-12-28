package app.services.impl;

import app.dtos.OrderImportDto;
import app.entities.BarTable;
import app.entities.Order;
import app.entities.User;
import app.entities.enums.OrderStatus;
import app.repositories.BarTableRepository;
import app.repositories.OrderRepository;
import app.repositories.UserRepository;
import app.services.api.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BarTableRepository barTableRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, BarTableRepository barTableRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.barTableRepository = barTableRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Order findOpenOrderByTable(Long tableId) {
        BarTable barTable = this.barTableRepository.getOne(tableId);
        return this.orderRepository.findOpenOrderByBarTable(barTable);
    }

    @Override
    @Transactional
    public void createNewOrder(OrderImportDto orderImportDto) {
        Order order = new Order();

        BarTable barTable = this.barTableRepository.findOne(orderImportDto.getTableId());
        User user = this.userRepository.findByName(orderImportDto.getUserName());

        this.barTableRepository.changeTableStatus(false, orderImportDto.getTableId());

        order.setBarTable(barTable);
        order.setUser(user);
        order.setStatus("Open");
        order.setDate(new Date());
        this.orderRepository.save(order);
    }

    @Override
    @Transactional
    public void closeOrder(Long orderId) {
        Order order = this.orderRepository.getOne(orderId);
        this.barTableRepository.changeTableStatus(true, order.getBarTable().getId());
        this.orderRepository.changeOrderStatus(OrderStatus.CLOSED, orderId);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = this.orderRepository.getOne(orderId);
        this.barTableRepository.changeTableStatus(true, order.getBarTable().getId());
        this.orderRepository.changeOrderStatus(OrderStatus.CANCELLED, orderId);
    }


}
