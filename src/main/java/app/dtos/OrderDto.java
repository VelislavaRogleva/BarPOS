package app.dtos;

import app.entities.BarTable;
import app.entities.Product;
import app.entities.User;
import app.enums.OrderStatus;

import java.util.*;


public class OrderDto {
    private Long orderId;
    private Date date;
    private OrderStatus status;
    private BarTable barTable;
    private User user;
    private Map<String, Integer> products;

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

    public Map<String, Integer> getProducts() {
        return products;
    }

    public void setProducts(Map<String, Integer> products) {
        this.products = products;
    }

    public void addProduct(String productName) {
        if (products.containsKey(productName)) {
            this.increaseQuantity(productName);
        } else {
            this.products.put(productName, 1);
        }

    }

    public void increaseQuantity(String productName) {
        if (products.containsKey(productName)) {
            Integer quantity = this.products.get(productName);
            this.products.put(productName, quantity + 1);
        }
    }

    public void decreaseQuantity(String productName) {
        if (products.containsKey(productName)) {
            Integer quantity = this.products.get(productName);
            if (quantity > 0) {
                this.products.put(productName, quantity - 1);
            }
        }
    }
}
