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
    private final OrderProductRepository orderProductRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, BarTableRepository barTableRepository, UserRepository userRepository, OrderProductRepository orderProductRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.barTableRepository = barTableRepository;
        this.orderProductRepository = orderProductRepository;
    }

    @Override
    public OrderDto findOpenOrderByTable(Long tableId) {
        BarTable barTable = this.barTableRepository.findOne(tableId);
        Order order = this.orderRepository.findOpenOrderByBarTable(barTable);
        OrderDto orderDto = null;
        if (order != null) {
            orderDto = createOrderDto(order);
            List<OrderProduct> orderProductList = this.orderProductRepository.findProductsInOrder(order.getId());
            Map<Product, Integer> products = new HashMap<>();
            for (OrderProduct orderProduct : orderProductList) {
                products.put(orderProduct.getId().getProduct(), orderProduct.getQuantity());
            }

            orderDto.setProducts(products);
        }
        return orderDto;
    }

    @Override
    public List<OrderDto> findOpenOrdersBetweenDates(Date startDate, Date endDate) {
        return this.findAllOrdersBetweenDates(OrderStatus.OPEN, startDate, endDate);
    }

    @Override
    public List<OrderDto> findCancelledOrdersBetweenDates(Date startDate, Date endDate) {
        return this.findAllOrdersBetweenDates(OrderStatus.CANCELLED, startDate, endDate);
    }

    @Override
    public List<OrderDto> findClosedOrdersBetweenDates(Date startDate, Date endDate) {
       return this.findAllOrdersBetweenDates(OrderStatus.CLOSED, startDate, endDate);
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
        Map<Product, Integer> products = orderDto.getProducts();
        Order order = this.orderRepository.findOne(orderDto.getOrderId());


        for (Product product: products.keySet()) {
            this.saveOrderProductToDb(product, products, order);
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


        Map<Product, Integer> products = orderDto.getProducts();
        for (Product product : products.keySet()) {
            this.saveOrderProductToDb(product, products, order);
        }

    }

    private void saveOrderProductToDb(Product product, Map<Product, Integer> products, Order order) {
        OrderProductId orderProductId = new OrderProductId();
        orderProductId.setProduct(product);
        orderProductId.setOrder(order);

        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(orderProductId);
        orderProduct.setQuantity(products.get(product));
        this.orderProductRepository.save(orderProduct);
    }

    private List<OrderDto> findAllOrdersBetweenDates(OrderStatus orderStatus, Date startDate, Date endDate) {
        List<Order> orders = this.orderRepository.findOrdersBetweenDates(orderStatus, startDate, endDate);
        List<OrderDto> orderDtos = new ArrayList<>();
        if (!orders.isEmpty()) {
            for (Order order : orders) {
                OrderDto orderDto = createOrderDto(order);
                List<OrderProduct> orderProductList = this.orderProductRepository.findProductsInOrder(order.getId());
                Map<Product, Integer> products = new HashMap<>();
                for (OrderProduct orderProduct : orderProductList) {
                    products.put(orderProduct.getId().getProduct(), orderProduct.getQuantity());
                }

                orderDto.setProducts(products);

                orderDtos.add(orderDto);
            }
        }

        return orderDtos;
    }


    private OrderDto createOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();

        orderDto.setDate(order.getDate());
        orderDto.setUser(order.getUser());
        orderDto.setBarTable(order.getBarTable());
        orderDto.setOrderId(order.getId());
        orderDto.setStatus(order.getStatus());
        return orderDto;
    }
}