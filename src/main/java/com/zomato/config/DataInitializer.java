package com.zomato.config;

import com.zomato.dao.RestaurantDao;
import com.zomato.dao.UserDao;
import com.zomato.model.Restaurant;
import com.zomato.model.MenuItem;
import com.zomato.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private JdbcTemplate jdbc;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Checking database seeding...");
        
        // 1. Seed Users if empty
        Integer userCount = jdbc.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        if (userCount != null && userCount == 0) {
            System.out.println("Seeding default users...");
            User u = new User();
            u.setName("Sahana Kulkarni");
            u.setUsername("sahana");
            u.setEmail("sahana@zomato.com");
            u.setPassword(BCrypt.hashpw("sahana@123", BCrypt.gensalt()));
            u.setRole("customer");
            userDao.save(u);
            System.out.println("Default user 'sahana' (password: sahana@123) seeded.");
        }

        // 2. Seed Restaurants and Menu Items if empty
        Integer restCount = jdbc.queryForObject("SELECT COUNT(*) FROM restaurants", Integer.class);
        if (restCount != null && restCount == 0) {
            System.out.println("Seeding restaurant data...");
            
            // Meghana Foods
            restaurantDao.saveRestaurant(new Restaurant(0, "Meghana Foods", 4.5, "25-30 min", 30, "Biryani, South Indian", "🍛", "Biryani"));
            int meghanaId = jdbc.queryForObject("SELECT id FROM restaurants WHERE name = ?", Integer.class, "Meghana Foods");
            restaurantDao.saveMenuItem(new MenuItem(0, meghanaId, "Meghana Special Chicken Biryani", "Our signature spicy chicken biryani made with premium basmati rice.", 320, false, "🍛", "Biryani"));
            restaurantDao.saveMenuItem(new MenuItem(0, meghanaId, "Paneer Biryani", "Fragrant basmati rice layered with spiced paneer cubes.", 280, true, "🍛", "Biryani"));
            restaurantDao.saveMenuItem(new MenuItem(0, meghanaId, "Chicken Boneless Kabab", "Deep-fried spicy chicken bites, a local favorite.", 240, false, "🍗", "Starter"));

            // Leon's Burgers
            restaurantDao.saveRestaurant(new Restaurant(0, "Leon's Burgers", 4.3, "20-25 min", 40, "Burger, Fast Food, Wraps", "🍔", "Burger"));
            int leonsId = jdbc.queryForObject("SELECT id FROM restaurants WHERE name = ?", Integer.class, "Leon's Burgers");
            restaurantDao.saveMenuItem(new MenuItem(0, leonsId, "Leon Grilled Chicken Burger", "Flame-grilled juicy chicken patty with mayo and lettuce.", 180, false, "🍔", "Burger"));
            restaurantDao.saveMenuItem(new MenuItem(0, leonsId, "Leon Classic Veg Burger", "Crispy veg patty with special sauce and lettuce.", 140, true, "🍔", "Burger"));
            restaurantDao.saveMenuItem(new MenuItem(0, leonsId, "Peri Peri French Fries", "Crispy fries tossed in hot peri-peri seasoning.", 110, true, "🍟", "Sides"));

            // Corner House
            restaurantDao.saveRestaurant(new Restaurant(0, "Corner House", 4.8, "15-20 min", 30, "Desserts, Ice Cream, Shakes", "🍰", "Desserts"));
            int cornerId = jdbc.queryForObject("SELECT id FROM restaurants WHERE name = ?", Integer.class, "Corner House");
            restaurantDao.saveMenuItem(new MenuItem(0, cornerId, "Death by Chocolate", "Double chocolate cake, ice cream, chocolate sauce, whipped cream and cherries.", 290, true, "🍰", "Desserts"));
            restaurantDao.saveMenuItem(new MenuItem(0, cornerId, "Hot Chocolate Fudge", "Two scoops of vanilla ice cream topped with rich hot chocolate fudge and peanuts.", 220, true, "🍦", "Desserts"));

            // Pizza Hut
            restaurantDao.saveRestaurant(new Restaurant(0, "Pizza Hut", 4.1, "30-35 min", 0, "Pizza, Fast Food", "🍕", "Pizza"));
            int pizzaId = jdbc.queryForObject("SELECT id FROM restaurants WHERE name = ?", Integer.class, "Pizza Hut");
            restaurantDao.saveMenuItem(new MenuItem(0, pizzaId, "Classic Margherita Pizza", "Simple but classic cheese pizza with freshly prepared dough.", 250, true, "🍕", "Pizza"));
            restaurantDao.saveMenuItem(new MenuItem(0, pizzaId, "Double Cheese Margherita", "Margherita pizza with an extra layer of melted mozzarella.", 320, true, "🍕", "Pizza"));
            restaurantDao.saveMenuItem(new MenuItem(0, pizzaId, "Chicken Supreme Pizza", "Topped with grilled chicken, spicy chicken sausage, mushrooms and onions.", 390, false, "🍕", "Pizza"));

            // Empire Restaurant
            restaurantDao.saveRestaurant(new Restaurant(0, "Empire Restaurant", 4.2, "25-30 min", 30, "Biryani, North Indian, Kebabs", "🍛", "Biryani"));
            int empireId = jdbc.queryForObject("SELECT id FROM restaurants WHERE name = ?", Integer.class, "Empire Restaurant");
            restaurantDao.saveMenuItem(new MenuItem(0, empireId, "Empire Chicken Biryani", "Classic layered chicken biryani cooked to perfection.", 290, false, "🍛", "Biryani"));
            restaurantDao.saveMenuItem(new MenuItem(0, empireId, "Ghee Rice", "Basmati rice cooked in pure ghee and topped with dry fruits.", 160, true, "🍚", "Biryani"));
            restaurantDao.saveMenuItem(new MenuItem(0, empireId, "Empire Butter Chicken", "Mildly sweet and spicy butter chicken with rich cream.", 310, false, "🍲", "Curry"));

            System.out.println("Restaurants and Menu Items seeded successfully.");
        }
    }
}
