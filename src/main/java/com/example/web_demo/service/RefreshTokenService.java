package com.example.web_demo.service;

import org.springframework.stereotype.Service;

import com.example.web_demo.entity.RefreshToken;
import com.example.web_demo.entity.User;
import com.example.web_demo.repository.RefreshTokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    
    public String generateRefreshToken(User user) {
        RefreshToken newToken = RefreshToken.createTokenForUser(user);
        refreshTokenRepository.save(newToken);
        return newToken.getToken();
    }
}
