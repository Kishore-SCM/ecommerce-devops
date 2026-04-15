package com.ecommerce.user.controller;

import com.ecommerce.user.dto.*;
import com.ecommerce.user.entity.User;
import com.ecommerce.user.security.JwtService;
import com.ecommerce.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {

    private final UserService userService;
    private final JwtService  jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        User user = userService.register(req);
        String token = jwtService.generate(user.getId(),
            Map.of("email", user.getEmail(), "roles", user.getRoles()));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new AuthResponse(token, user.getId(), user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        User user = userService.authenticate(req.email(), req.password());
        String token = jwtService.generate(user.getId(),
            Map.of("email", user.getEmail(), "roles", user.getRoles()));
        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getEmail()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }
}
