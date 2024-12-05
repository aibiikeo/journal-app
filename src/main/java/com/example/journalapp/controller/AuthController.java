package com.example.journalapp.controller;

import com.example.journalapp.dto.AuthDto;
import com.example.journalapp.entity.User;
import com.example.journalapp.security.JwtTokenProvider;
import com.example.journalapp.service.AuthService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/trusted/auth")
@Data
@Slf4j
public class AuthController {
    private final AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody AuthDto dto) {
        try {
            User user = authService.signup(dto);
            log.info("User registered successfully: {}", user.getEmail());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody AuthDto authDto) {
        try {
            // Validate the user credentials (email and password)
            if (authService.validateUserCredentials(authDto)) {
                // Generate the JWT token
                String token = jwtTokenProvider.generateToken(authDto.getEmail());

                // Log successful login
                log.info("User logged in successfully: {}", authDto.getEmail());

                // Prepare the response with the token
                Map<String, String> response = new HashMap<>();
                response.put("token", "Bearer " + token);  // Return the token with Bearer prefix
                return ResponseEntity.ok(response);
            } else {
                // Invalid credentials
                log.error("Invalid credentials for user: {}", authDto.getEmail());
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid credentials");
                return ResponseEntity.status(401).body(errorResponse);  // Unauthorized
            }
        } catch (Exception e) {
            // Handle unexpected errors
            log.error("Error during login: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred during login");
            return ResponseEntity.status(500).body(errorResponse);  // Internal server error
        }
    }
}
