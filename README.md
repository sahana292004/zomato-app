# Zomato — Full-Stack Food Delivery Application

VoteEase-inspired, premium single-page **Zomato Food Delivery Application** built using **Spring Boot (Java)**, **MySQL**, **JavaScript (ES6+)**, **HTML5**, and **CSS3**.

The application is structured around a real RESTful API backend and a fully stateful, animated frontend with modern glassmorphism styling, a simulated real-time order tracking workflow, user authentication, and persistent order history.

---

## 🚀 Key Features

* **User Authentication**: Secure Sign-in and Registration using password cryptography (jBCrypt hashing).
* **Restaurant & Menu Discovery**: Browse local restaurants by categories (Biryani, Pizza, Burger, Sushi, Desserts, Chinese, South Indian) or search by keywords.
* **Persistent Cart Manager**: Dynamic adding, quantity incrementing/decrementing, and automated pricing calculations (item totals, delivery fee, taxes, and grand total).
* **Simulated Real-Time Tracking**: Placing an order triggers a simulated delivery tracking system (Order Confirmed → Preparing Food → Out for Delivery → Delivered) that updates the database dynamically.
* **Profile & Order History**: View user profile details and list past orders with items and timestamps directly from the database.
* **Theme Customization**: Responsive dark-mode styling with high visual appeal.

---

## 🛠️ Technology Stack

* **Backend**: Java 17+, Spring Boot 3.x, Spring JDBC (`JdbcTemplate`)
* **Database**: MySQL (relational schemas, foreign key constraints, unique keys)
* **Frontend**: HTML5, Vanilla CSS3 (custom variables, Flexbox & Grid layouts, glassmorphism), Vanilla JavaScript (ES6+, Fetch API, async/await)
* **Security**: jBCrypt (password encryption), Role-Based Access Control (RBAC)

---

## 📊 Database Schema

The database consists of 5 tables optimized for relational integrity:

1. **`users`**: Manages customer profiles, emails, and hashed credentials.
2. **`restaurants`**: Stores restaurant info, ratings, and delivery times.
3. **`menu_items`**: Contains menus with descriptions, prices, categories, and dietary tags.
4. **`orders`**: Records order totals, delivery fees, taxes, and status.
5. **`order_items`**: Bridges ordered items with quantity and price mappings.

---

## 🖥️ How to Run Locally

### Prerequisites
* Java JDK 17 or higher
* MySQL Server running
* Maven installed

### Setup & Run
1. Create a MySQL database or let Spring Boot build it automatically.
2. Configure your MySQL credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=YOUR_MYSQL_USERNAME
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```
3. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```
4. Open your browser and navigate to `http://localhost:8080`.
