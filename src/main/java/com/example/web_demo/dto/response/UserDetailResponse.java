package com.example.web_demo.dto.response;

import java.time.LocalDateTime;

import com.example.web_demo.entity.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailResponse {
    private String username;
    private String email;
    private Boolean isVerified;
    private LocalDateTime lastLogin;

    public static UserDetailResponse of(User user) {
        return UserDetailResponse
            .builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .isVerified(user.isEmailVerified())
            .lastLogin(user.getLastLoginAt())
            .build();
    }
}
