package com.zomato.dao;

import com.zomato.model.Order;
import com.zomato.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class OrderDao {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<Order> orderMapper = new RowMapper<Order>() {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order o = new Order();
            o.setId(rs.getInt("id"));
            o.setUserId(rs.getInt("user_id"));
            o.setRestaurantName(rs.getString("restaurant_name"));
            o.setStatus(rs.getString("status"));
            o.setSubtotal(rs.getInt("subtotal"));
            o.setDeliveryFee(rs.getInt("delivery_fee"));
            o.setTaxes(rs.getInt("taxes"));
            o.setTotal(rs.getInt("total"));
            o.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return o;
        }
    };

    private final RowMapper<OrderItem> orderItemMapper = new RowMapper<OrderItem>() {
        @Override
        public OrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new OrderItem(
                rs.getInt("id"),
                rs.getInt("order_id"),
                rs.getString("name"),
                rs.getInt("price"),
                rs.getInt("quantity")
            );
        }
    };

    @Transactional
    public boolean save(Order o) {
        String sql = "INSERT INTO orders (user_id, restaurant_name, status, subtotal, delivery_fee, taxes, total) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        int rows = jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, o.getUserId());
            ps.setString(2, o.getRestaurantName());
            ps.setString(3, o.getStatus());
            ps.setInt(4, o.getSubtotal());
            ps.setInt(5, o.getDeliveryFee());
            ps.setInt(6, o.getTaxes());
            ps.setInt(7, o.getTotal());
            return ps;
        }, keyHolder);
        
        if (rows > 0 && keyHolder.getKey() != null) {
            int orderId = keyHolder.getKey().intValue();
            o.setId(orderId);
            for (OrderItem item : o.getItems()) {
                String itemSql = "INSERT INTO order_items (order_id, name, price, quantity) VALUES (?, ?, ?, ?)";
                jdbc.update(itemSql, orderId, item.getName(), item.getPrice(), item.getQuantity());
            }
            return true;
        }
        return false;
    }

    public List<Order> findByUserId(int userId) {
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        List<Order> list = jdbc.query(sql, orderMapper, userId);
        for (Order o : list) {
            o.setItems(findItemsByOrderId(o.getId()));
        }
        return list;
    }

    public List<OrderItem> findItemsByOrderId(int orderId) {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        return jdbc.query(sql, orderItemMapper, orderId);
    }

    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        int rows = jdbc.update(sql, status, orderId);
        return rows > 0;
    }
}
