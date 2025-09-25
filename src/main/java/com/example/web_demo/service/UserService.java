package com.example.web_demo.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.web_demo.entity.User;
import com.example.web_demo.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
