package com.zomato.dao;

import com.zomato.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbc;

    private final RowMapper<User> userMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setName(rs.getString("name"));
            u.setUsername(rs.getString("username"));
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
            u.setRole(rs.getString("role"));
            u.setJoined(rs.getDate("joined").toLocalDate());
            return u;
        }
    };

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> list = jdbc.query(sql, userMapper, username);
        return list.isEmpty() ? null : list.get(0);
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> list = jdbc.query(sql, userMapper, email);
        return list.isEmpty() ? null : list.get(0);
    }

    public boolean save(User u) {
        String sql = "INSERT INTO users (name, username, email, password, role) VALUES (?, ?, ?, ?, ?)";
        int rows = jdbc.update(sql, u.getName(), u.getUsername(), u.getEmail(), u.getPassword(), u.getRole());
        return rows > 0;
    }
}
