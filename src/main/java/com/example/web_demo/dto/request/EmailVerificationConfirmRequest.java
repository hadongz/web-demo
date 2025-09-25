package com.example.web_demo.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmailVerificationConfirmRequest {

    @NotEmpty(message = "Verification token cannot be empty")
    private String verificationToken;
}
