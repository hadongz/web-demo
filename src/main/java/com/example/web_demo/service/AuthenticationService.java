package com.example.web_demo.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.web_demo.entity.User;
import com.example.web_demo.exception.AccountLockedException;
import com.example.web_demo.exception.UserAlreadyExistsException;
import com.example.web_demo.exception.WrongPasswordException;
import com.example.web_demo.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build();

        return userRepository.save(user);
    }

    public Optional<User> login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.isAccountLocked()) {
                throw new AccountLockedException();
            }

            if (passwordEncoder.matches(password, userOptional.get().getPassword())) {
                return userOptional;
            } else {
                user.increamentFailedLogin();
                throw new WrongPasswordException();
            }
        }

        return Optional.empty();
    }
}
