package com.ecommerce.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String phone;
    private String address;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    private Boolean active;
    private Boolean emailVerified;

    @CreationTimestamp private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public enum Role { ROLE_USER, ROLE_ADMIN }
}
