package com.example.journalapp.controller;

import com.example.journalapp.dto.PasswordResetDto;
import com.example.journalapp.entity.PasswordResetToken;
import com.example.journalapp.entity.User;
import com.example.journalapp.repository.PasswordResetTokenRepository;
import com.example.journalapp.service.AuthService;
import com.example.journalapp.service.EmailService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/password-reset")
@Data
@Slf4j
public class PasswordResetController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    // Endpoint to initiate password reset
    @PostMapping("/request/{userId}")
    public String requestPasswordReset(@PathVariable Long userId) {
        Optional<User> user = authService.findById(userId);  // Fetch user by userId
        if (user.isPresent()) {
            // Generate a unique reset token
            String token = UUID.randomUUID().toString();

            // Save the token in the database with user association
            PasswordResetToken resetToken = new PasswordResetToken(token, user.get());
            tokenRepository.save(resetToken);

            // Send the reset link to the user's email
            emailService.sendEmail(user.get().getEmail(), "Password Reset Request", "Reset token: " + token);

            return "Password reset email sent!";
        } else {
            return "No user found with id " + userId;
        }
    }

    // Endpoint to reset password using token
    @PostMapping("/reset")
    public String resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        String token = passwordResetDto.getToken();
        String newPassword = passwordResetDto.getNewPassword();

        Optional<PasswordResetToken> resetTokenOpt = tokenRepository.findByToken(token);
        if (resetTokenOpt.isPresent()) {
            PasswordResetToken resetToken = resetTokenOpt.get();

            // Check if token is expired
            if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                return "Token has expired!";
            }

            // Get user and update password
            User user = resetToken.getUser();
            user.setPassword(newPassword); // You should hash the password before saving
            authService.updateUser(user);  // Implement the updateUser method in your UserService

            // Delete the token after it's used
            tokenRepository.delete(resetToken);

            return "Password has been reset successfully.";
        } else {
            return "Invalid or expired token.";
        }
    }

}
