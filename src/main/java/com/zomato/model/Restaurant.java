package com.zomato.model;

import java.util.List;

public class Restaurant {
    private int id;
    private String name;
    private double rating;
    private String deliveryTime;
    private int deliveryFee;
    private String tags;
    private String emoji;
    private String category;
    private List<MenuItem> menuItems;

    // Constructors
    public Restaurant() {}

    public Restaurant(int id, String name, double rating, String deliveryTime, int deliveryFee, String tags, String emoji, String category) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.deliveryTime = deliveryTime;
        this.deliveryFee = deliveryFee;
        this.tags = tags;
        this.emoji = emoji;
        this.category = category;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }

    public int getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(int deliveryFee) { this.deliveryFee = deliveryFee; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<MenuItem> getMenuItems() { return menuItems; }
    public void setMenuItems(List<MenuItem> menuItems) { this.menuItems = menuItems; }
}
