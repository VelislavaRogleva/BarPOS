package app.services.api;

import app.dtos.OrderDto;
import app.entities.Order;

import javax.transaction.Transactional;

public interface OrderService {
    OrderDto findOpenOrderByTable(Long tableId);

    @Transactional
    void createOrUpdateOrder(OrderDto orderDto);

    void closeOrder(Long orderId);

    void cancelOrder(Long orderId);
}