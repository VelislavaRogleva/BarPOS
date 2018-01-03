package app.dtos;

import app.entities.BarTable;
import app.entities.Product;
import app.entities.User;
import app.enums.OrderStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class OrderDto {
    private Long orderId;
    private Date date;
    private OrderStatus status;
    private BarTable barTable;
    private User user;
    private Map<Product, Integer> products;

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

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public void setProducts(Map<Product, Integer> products) {
        this.products = products;
    }

    public void addProduct(Product product) {
        this.products.putIfAbsent(product, 1);
    }

    public void increaseQuantity(Product product) {
        this.products.put(product, this.products.get(product) + 1);
    }


}
