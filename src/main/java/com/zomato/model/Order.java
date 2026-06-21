package com.zomato.model;

import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private int id;
    private int userId;
    private String restaurantName;
    private String status;
    private int subtotal;
    private int deliveryFee;
    private int taxes;
    private int total;
    private LocalDateTime createdAt;
    private List<OrderItem> items;

    // Constructors
    public Order() {}

    public Order(int id, int userId, String restaurantName, String status, int subtotal, int deliveryFee, int taxes, int total, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.restaurantName = restaurantName;
        this.status = status;
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.taxes = taxes;
        this.total = total;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getSubtotal() { return subtotal; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }

    public int getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(int deliveryFee) { this.deliveryFee = deliveryFee; }

    public int getTaxes() { return taxes; }
    public void setTaxes(int taxes) { this.taxes = taxes; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
