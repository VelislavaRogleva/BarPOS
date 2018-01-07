package app.services.api;

import app.dtos.OrderDto;
import app.entities.Order;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface OrderService {
    OrderDto findOpenOrderByTable(Long tableId);

    @Transactional
    void createOrUpdateOrder(OrderDto orderDto);

    List<OrderDto> findOpenOrdersBetweenDates(Date startDate, Date endDate);

    List<OrderDto> findCancelledOrdersBetweenDates(Date startDate, Date endDate);

    List<OrderDto> findClosedOrdersBetweenDates(Date startDate, Date endDate);

    void closeOrder(Long orderId);

    void cancelOrder(Long orderId);
}