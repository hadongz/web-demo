package com.example.web_demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "Token Cannot be Empty")
    private String refreshToken;
}
