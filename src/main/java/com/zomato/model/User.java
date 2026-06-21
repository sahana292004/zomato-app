package com.zomato.model;

import java.time.LocalDate;

public class User {
    private int id;
    private String name;
    private String username;
    private String email;
    private String password;
    private String role;
    private LocalDate joined;

    // Constructors
    public User() {}

    public User(int id, String name, String username, String email, String password, String role, LocalDate joined) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.joined = joined;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDate getJoined() { return joined; }
    public void setJoined(LocalDate joined) { this.joined = joined; }
}
