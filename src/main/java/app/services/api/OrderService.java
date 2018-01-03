package app.services.api;

import app.dtos.OrderDto;
import app.entities.Order;

public interface OrderService {
    Order findOpenOrderByTable(Long tableId);

    void createNewOrder(OrderDto orderDto);

    void closeOrder(Long orderId);

    void cancelOrder(Long orderId);
}