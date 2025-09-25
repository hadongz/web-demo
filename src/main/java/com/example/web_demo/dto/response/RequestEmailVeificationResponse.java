package com.example.web_demo.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestEmailVeificationResponse {
    private String token;

    public static RequestEmailVeificationResponse of(String token) {
        return RequestEmailVeificationResponse
            .builder()
            .token(token)
            .build();
    }
}
