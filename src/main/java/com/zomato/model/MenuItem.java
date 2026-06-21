package com.zomato.model;

public class MenuItem {
    private int id;
    private int restaurantId;
    private String name;
    private String description;
    private int price;
    private boolean isVeg;
    private String emoji;
    private String category;

    // Constructors
    public MenuItem() {}

    public MenuItem(int id, int restaurantId, String name, String description, int price, boolean isVeg, String emoji, String category) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isVeg = isVeg;
        this.emoji = emoji;
        this.category = category;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public boolean isVeg() { return isVeg; }
    public void setVeg(boolean veg) { isVeg = veg; }

    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
