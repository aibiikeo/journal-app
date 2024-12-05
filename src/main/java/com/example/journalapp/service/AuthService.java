package com.example.journalapp.service;

import com.example.journalapp.dto.AuthDto;
import com.example.journalapp.entity.User;
import com.example.journalapp.repository.UserRepository;
import com.example.journalapp.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Data
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public User signup(@Valid AuthDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("User already exists with email: " + dto.getEmail());
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    public boolean validateUserCredentials(AuthDto authDto) {
        Optional<User> userOptional = userRepository.findByEmail(authDto.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (passwordEncoder.matches(authDto.getPassword(), user.getPassword())) {
                log.info("User {} logged in successfully", authDto.getEmail());
                return true;
            } else {
                log.warn("Incorrect password attempt for user: {}", authDto.getEmail());
                return false; // Incorrect password
            }
        } else {
            log.warn("User not found with email: {}", authDto.getEmail());
            return false;  // User not found
        }
    }
}

