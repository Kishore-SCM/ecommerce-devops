package com.ecommerce.user.service;

import com.ecommerce.user.dto.*;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.repository.UserRepository;
import com.ecommerce.user.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Service @RequiredArgsConstructor @Slf4j @Transactional
public class UserService {

    private final UserRepository  userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService      jwtService;

    public User register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.email()))
            throw new RuntimeException("Email already registered: " + req.email());
        User user = User.builder()
            .email(req.email()).password(passwordEncoder.encode(req.password()))
            .firstName(req.firstName()).lastName(req.lastName())
            .roles(Set.of(User.Role.ROLE_USER)).active(true).emailVerified(false).build();
        return userRepo.save(user);
    }

    public User authenticate(String email, String password) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new RuntimeException("Invalid credentials");
        user.setLastLoginAt(LocalDateTime.now());
        return userRepo.save(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(String userId) {
        User u = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponse(u.getId(), u.getEmail(), u.getFirstName(), u.getLastName(),
            u.getRoles(), u.getCreatedAt());
    }

    public AuthResponse refreshToken(String token) {
        String userId = jwtService.validate(token).getSubject();
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        String newToken = jwtService.generate(user.getId(),
            Map.of("email", user.getEmail(), "roles", user.getRoles()));
        return new AuthResponse(newToken, user.getId(), user.getEmail());
    }
}
