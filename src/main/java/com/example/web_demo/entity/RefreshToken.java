package com.example.web_demo.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refresh_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_revoked", nullable = false)
    @Builder.Default
    private boolean isRevoked = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Boolean isTokenExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public Boolean isValid() {
        return !isRevoked && !isTokenExpired();
    }

    public static RefreshToken createTokenForUser(User user) {
        return RefreshToken
            .builder()
            .token(UUID.randomUUID().toString())
            .user(user)
            .expiresAt(LocalDateTime.now().plusDays(3))
            .build();
    }
}
