package app.services.api;

import app.dtos.OrderImportDto;
import app.entities.Order;

public interface OrderService {
    Order findOpenOrderByTable(Long tableId);

    void createNewOrder(OrderImportDto orderImportDto);

    void closeOrder(Long orderId);

    void cancelOrder(Long orderId);
}