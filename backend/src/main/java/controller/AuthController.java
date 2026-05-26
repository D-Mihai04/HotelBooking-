package controller;

import model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.BookingService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final BookingService bookingService = new BookingService();


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email    = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "email and password required"));
        }

        User user = bookingService.loginUser(email, password);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }


        user.setPassword(null);
        return ResponseEntity.ok(user);
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String name     = body.get("name");
        String email    = body.get("email");
        String password = body.get("password");

        if (name == null || email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "name, email and password required"));
        }

        User user = bookingService.registerUser(name, email, password);
        if (user == null) {
            return ResponseEntity.status(409).body(Map.of("error", "Email already in use"));
        }

        user.setPassword(null);
        return ResponseEntity.ok(user);
    }
}
