package com.example.web_demo.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String message;
    private Long userId;
    private String username;
    private String token;
    private String refreshToken;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public static AuthResponse success(String message, Long userId, String username) {
        return AuthResponse
         .builder()
         .message(message)
         .userId(userId)
         .username(username)
         .timestamp(LocalDateTime.now())
         .build();
    }

    public static AuthResponse successWithToken(String message, Long userId, String username, String token, String refreshToken) {
        return AuthResponse
         .builder()
         .message(message)
         .userId(userId)
         .username(username)
         .token(token)
         .refreshToken(refreshToken)
         .timestamp(LocalDateTime.now())
         .build();
    }
}
