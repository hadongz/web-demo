package com.example.web_demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_demo.dto.request.LoginRequest;
import com.example.web_demo.dto.request.RegisterRequest;
import com.example.web_demo.dto.response.ApiErrorResponse;
import com.example.web_demo.dto.response.AuthResponse;
import com.example.web_demo.entity.User;
import com.example.web_demo.service.AuthenticationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authenctation API")
public class AuthenticationController {

    private final AuthenticationService authService;

    @Operation(summary = "Register new user", description = "Create a new user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "409", description = "Username already exists"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request.getUsername(), request.getPassword());
        
        AuthResponse response = AuthResponse.success(
            "User registered successfully", 
            user.getId(), 
            user.getUsername()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "User login", description = "Authenticate user and return user info")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User logged in successfully"),
        @ApiResponse(responseCode = "403", description = "Login failed, wrong username or password")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Optional<User> userOpt = authService.login(request.getUsername(), request.getPassword());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            AuthResponse response = AuthResponse.success(
                "User logged in successfully", 
                user.getId(), 
                user.getUsername()
            );

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        ApiErrorResponse errorRes = ApiErrorResponse.of(
            "Login Error",
            "Login error, wrong username or password", 
            HttpStatus.UNAUTHORIZED.value(), 
            httpRequest.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorRes);
    }
}
