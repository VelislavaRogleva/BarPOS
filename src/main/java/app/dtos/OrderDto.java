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
        String productName = product.getName();
        List<String> productsInOrder = this.productsInOrder();

        if (productsInOrder.contains(productName)) {
            Product prdct = this.productFromMap(productName);
            this.increaseQuantity(prdct);
        } else {
            this.products.put(product, 1);
        }

    }

    public void increaseQuantity(Product product) {

        String productName = product.getName();
        List<String> productsInOrder = this.productsInOrder();

        if (productsInOrder.contains(productName)) {
            Product prdct = this.productFromMap(productName);
            Integer quantity = this.products.get(prdct);
            this.products.put(prdct, quantity + 1);
        }
    }

    public void decreaseQuantity(Product product) {
        String productName = product.getName();
        List<String> productsInOrder = this.productsInOrder();
        if (productsInOrder.contains(productName)) {
            Product prdct = this.productFromMap(productName);
            Integer quantity = this.products.get(prdct);
            if (quantity > 0) {
                this.products.put(prdct, quantity - 1);
            }
        }
    }

    private List<String> productsInOrder() {
        List<String> result = new ArrayList<>();
        for (Product p : products.keySet()) {
            result.add(p.getName());
        }
        return result;
    }

    private Product productFromMap(String productName) {
        Product result = null;
        for (Product product1 : products.keySet()) {
            if (product1.getName().equals(productName)) {
                result = product1;
                break;
            }
        }
        return result;
    }
}
