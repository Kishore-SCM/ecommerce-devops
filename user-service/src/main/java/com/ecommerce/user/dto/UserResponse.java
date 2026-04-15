package com.ecommerce.user.dto;
import com.ecommerce.user.entity.User;
import java.time.LocalDateTime;
import java.util.Set;
public record UserResponse(
    String id, String email, String firstName, String lastName,
    Set<User.Role> roles, LocalDateTime createdAt
) {}
