package com.zomato.controller;

import com.zomato.dao.OrderDao;
import com.zomato.model.Order;
import com.zomato.model.OrderItem;
import com.zomato.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderDao orderDao;

    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> body, HttpSession session) {
        User u = (User) session.getAttribute("user");
        Map<String, Object> resp = new HashMap<>();
        if (u == null) {
            resp.put("error", "Unauthorized. Please sign in.");
            return ResponseEntity.status(401).body(resp);
        }

        String restName = (String) body.get("restaurantName");
        List<Map<String, Object>> itemsList = (List<Map<String, Object>>) body.get("items");
        
        if (restName == null || itemsList == null || itemsList.isEmpty()) {
            resp.put("error", "Invalid order payload.");
            return ResponseEntity.badRequest().body(resp);
        }

        // Calculate pricing
        int subtotal = 0;
        List<OrderItem> orderItems = new ArrayList<>();
        for (Map<String, Object> itemMap : itemsList) {
            String name = (String) itemMap.get("name");
            int price = ((Number) itemMap.get("price")).intValue();
            int qty = ((Number) itemMap.get("quantity")).intValue();
            subtotal += price * qty;
            
            OrderItem item = new OrderItem();
            item.setName(name);
            item.setPrice(price);
            item.setQuantity(qty);
            orderItems.add(item);
        }

        int deliveryFee = subtotal > 499 ? 0 : 30;
        int taxes = (int) Math.round(subtotal * 0.05);
        int total = subtotal + deliveryFee + taxes;

        Order o = new Order();
        o.setUserId(u.getId());
        o.setRestaurantName(restName);
        o.setStatus("Order Confirmed");
        o.setSubtotal(subtotal);
        o.setDeliveryFee(deliveryFee);
        o.setTaxes(taxes);
        o.setTotal(total);
        o.setItems(orderItems);

        if (orderDao.save(o)) {
            resp.put("success", true);
            resp.put("orderId", o.getId());
            resp.put("total", total);
            return ResponseEntity.ok(resp);
        } else {
            resp.put("error", "Failed to place order.");
            return ResponseEntity.internalServerError().body(resp);
        }
    }

    @GetMapping
    public ResponseEntity<?> getOrderHistory(HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        return ResponseEntity.ok(orderDao.findByUserId(u.getId()));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> getOrderStatus(@PathVariable int id, HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }
        
        // Simulating status change based on time or direct call
        // Normally status is stored in DB. We can update status incrementally.
        // For simplicity, we can fetch it, and let frontend simulate or let this endpoint update status
        return ResponseEntity.ok(Map.of("orderId", id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable int id, @RequestBody Map<String, String> body, HttpSession session) {
        String status = body.get("status");
        Map<String, Object> resp = new HashMap<>();
        if (status == null) {
            resp.put("error", "Status field is required");
            return ResponseEntity.badRequest().body(resp);
        }
        if (orderDao.updateStatus(id, status)) {
            resp.put("success", true);
            resp.put("status", status);
            return ResponseEntity.ok(resp);
        } else {
            resp.put("error", "Failed to update status");
            return ResponseEntity.internalServerError().body(resp);
        }
    }
}
