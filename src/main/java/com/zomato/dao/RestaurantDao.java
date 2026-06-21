package com.zomato.dao;

import com.zomato.model.Restaurant;
import com.zomato.model.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class RestaurantDao {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<Restaurant> restaurantMapper = new RowMapper<Restaurant>() {
        @Override
        public Restaurant mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Restaurant(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getDouble("rating"),
                rs.getString("delivery_time"),
                rs.getInt("delivery_fee"),
                rs.getString("tags"),
                rs.getString("emoji"),
                rs.getString("category")
            );
        }
    };

    private final RowMapper<MenuItem> menuItemMapper = new RowMapper<MenuItem>() {
        @Override
        public MenuItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MenuItem(
                rs.getInt("id"),
                rs.getInt("restaurant_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("price"),
                rs.getBoolean("is_veg"),
                rs.getString("emoji"),
                rs.getString("category")
            );
        }
    };

    public List<Restaurant> findAll() {
        String sql = "SELECT * FROM restaurants";
        return jdbc.query(sql, restaurantMapper);
    }

    public Restaurant findById(int id) {
        String sql = "SELECT * FROM restaurants WHERE id = ?";
        List<Restaurant> list = jdbc.query(sql, restaurantMapper, id);
        if (list.isEmpty()) return null;
        
        Restaurant r = list.get(0);
        r.setMenuItems(findMenuItemsByRestaurantId(id));
        return r;
    }

    public List<MenuItem> findMenuItemsByRestaurantId(int restaurantId) {
        String sql = "SELECT * FROM menu_items WHERE restaurant_id = ?";
        return jdbc.query(sql, menuItemMapper, restaurantId);
    }

    public boolean saveRestaurant(Restaurant r) {
        String sql = "INSERT INTO restaurants (name, rating, delivery_time, delivery_fee, tags, emoji, category) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int rows = jdbc.update(sql, r.getName(), r.getRating(), r.getDeliveryTime(), r.getDeliveryFee(), r.getTags(), r.getEmoji(), r.getCategory());
        return rows > 0;
    }

    public boolean saveMenuItem(MenuItem m) {
        String sql = "INSERT INTO menu_items (restaurant_id, name, description, price, is_veg, emoji, category) VALUES (?, ?, ?, ?, ?, ?, ?)";
        int rows = jdbc.update(sql, m.getRestaurantId(), m.getName(), m.getDescription(), m.getPrice(), m.isVeg(), m.getEmoji(), m.getCategory());
        return rows > 0;
    }
}
