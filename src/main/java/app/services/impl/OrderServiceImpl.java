package app.services.impl;

import app.dtos.OrderDto;
import app.entities.*;
import app.enums.OrderStatus;
import app.repositories.*;
import app.services.api.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final BarTableRepository barTableRepository;
    private final UserRepository userRepository;
    private final OrderProductRepository orderProductRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, BarTableRepository barTableRepository, UserRepository userRepository, OrderProductRepository orderProductRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.barTableRepository = barTableRepository;
        this.userRepository = userRepository;
        this.orderProductRepository = orderProductRepository;
        this.productRepository = productRepository;
    }

    @Override
    public OrderDto findOpenOrderByTable(Long tableId) {
        BarTable barTable = this.barTableRepository.findOne(tableId);
        Order order = this.orderRepository.findOpenOrderByBarTable(barTable);
        OrderDto orderDto = null;
        if (order != null) {
            orderDto = new OrderDto();
            orderDto.setDate(order.getDate());
            orderDto.setUser(order.getUser());
            orderDto.setBarTable(barTable);
            orderDto.setOrderId(order.getId());
            orderDto.setStatus(order.getStatus());
            List<OrderProduct> orderProductList = this.orderProductRepository.findProductsInOrder(order.getId());
            Map<String, Integer> products = new HashMap<>();
            for (OrderProduct orderProduct : orderProductList) {
                products.put(orderProduct.getId().getProduct().getName(), orderProduct.getQuantity());
            }

            orderDto.setProducts(products);
        }
        return orderDto;
    }

    @Override
    @Transactional
    public void closeOrder(Long orderId) {
        Order order = this.orderRepository.findOne(orderId);
        this.barTableRepository.changeTableStatus(true, order.getBarTable().getId());
        this.orderRepository.changeOrderStatus(OrderStatus.CLOSED, orderId);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = this.orderRepository.findOne(orderId);
        this.barTableRepository.changeTableStatus(true, order.getBarTable().getId());
        this.orderRepository.changeOrderStatus(OrderStatus.CANCELLED, orderId);
    }

    @Override
    @Transactional
    public void createOrUpdateOrder(OrderDto orderDto) {
        if (orderDto.getOrderId() != null) {
            this.updateOrder(orderDto);
        } else {
            this.createNewOrder(orderDto);
        }
    }

    private void updateOrder(OrderDto orderDto) {
        Map<String, Integer> products = orderDto.getProducts();

        Order order = this.orderRepository.findOne(orderDto.getOrderId());

        for (String productName : products.keySet()) {
            this.saveOrderProductToDb(productName, products, order);
        }
    }


    private void createNewOrder(OrderDto orderDto) {
        Order order = new Order();
        BarTable barTable = orderDto.getBarTable();
        User user = orderDto.getUser();

        //make bar table unavailable when opening new order
        this.barTableRepository.changeTableStatus(false, orderDto.getBarTable().getId());

        order.setBarTable(barTable);
        order.setUser(user);
        order.setStatus("Open");
        order.setDate(new Date());

        this.orderRepository.save(order);


        Map<String, Integer> products = orderDto.getProducts();
        for (String productName : products.keySet()) {
            this.saveOrderProductToDb(productName, products, order);
        }

    }

    private void saveOrderProductToDb(String productName, Map<String, Integer> products, Order order) {
        OrderProductId orderProductId = new OrderProductId();
        Product product = this.productRepository.findByName(productName);
        orderProductId.setProduct(product);
        orderProductId.setOrder(order);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(orderProductId);
        orderProduct.setQuantity(products.get(productName));
        this.orderProductRepository.save(orderProduct);
    }


}