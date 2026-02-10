package com.RH.rh_digital_twin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Simple test authentication - in production, validate against database
        if ("admin".equals(username) && "password".equals(password)) {
            // Generate a simple JWT-like token for testing
            String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTUxNjIzOTAyMn0.test_token_for_sprint_demo";

            return ResponseEntity.ok(Map.of("token", token, "role", "ADMIN"));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }
}
