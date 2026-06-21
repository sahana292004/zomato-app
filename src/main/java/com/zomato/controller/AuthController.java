package com.zomato.controller;

import com.zomato.dao.UserDao;
import com.zomato.model.User;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserDao userDao;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");
        
        Map<String, Object> resp = new HashMap<>();
        if (name == null || username == null || email == null || password == null) {
            resp.put("error", "All fields are required.");
            return ResponseEntity.badRequest().body(resp);
        }
        
        if (userDao.findByUsername(username) != null) {
            resp.put("error", "Username already taken.");
            return ResponseEntity.badRequest().body(resp);
        }
        
        if (userDao.findByEmail(email) != null) {
            resp.put("error", "Email already registered.");
            return ResponseEntity.badRequest().body(resp);
        }
        
        User u = new User();
        u.setName(name);
        u.setUsername(username);
        u.setEmail(email);
        u.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        u.setRole("customer");
        
        if (userDao.save(u)) {
            resp.put("success", true);
            resp.put("message", "User registered successfully.");
            return ResponseEntity.ok(resp);
        } else {
            resp.put("error", "Failed to register user.");
            return ResponseEntity.internalServerError().body(resp);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body, HttpSession session) {
        String username = body.get("username");
        String password = body.get("password");
        
        Map<String, Object> resp = new HashMap<>();
        if (username == null || password == null) {
            resp.put("error", "Username and password required.");
            return ResponseEntity.badRequest().body(resp);
        }
        
        User u = userDao.findByUsername(username);
        if (u == null || !BCrypt.checkpw(password, u.getPassword())) {
            resp.put("error", "Invalid username or password.");
            return ResponseEntity.status(401).body(resp);
        }
        
        session.setAttribute("user", u);
        resp.put("success", true);
        resp.put("user", u);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpSession session) {
        User u = (User) session.getAttribute("user");
        if (u == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        return ResponseEntity.ok(u);
    }
}
