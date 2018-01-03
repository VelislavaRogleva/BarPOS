package app.dtos;

import app.entities.BarTable;
import app.entities.Product;
import app.entities.User;
import app.entities.enums.OrderStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class OrderDto {
    private Long orderId;
    private Date date;
    private OrderStatus status;
    private BarTable barTable;
    private User user;
    private Map<Long, Integer> products;

    public OrderDto() {
        this.products = new HashMap<>();
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BarTable getBarTable() {
        return barTable;
    }

    public void setBarTable(BarTable barTable) {
        this.barTable = barTable;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Map<Long, Integer> getProducts() {
        return products;
    }

    public void setProducts(Map<Long, Integer> products) {
        this.products = products;
    }

    public void addProduct(Long productId) {
        this.products.putIfAbsent(productId, 1);
    }

    public void increaseQuantity(Long productId) {
        this.products.put(productId, this.products.get(productId) + 1);
    }


}
