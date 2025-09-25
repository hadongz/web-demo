package com.example.web_demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_demo.dto.request.LoginRequest;
import com.example.web_demo.dto.request.RefreshTokenRequest;
import com.example.web_demo.dto.request.RegisterRequest;
import com.example.web_demo.dto.response.ApiErrorResponse;
import com.example.web_demo.dto.response.AuthResponse;
import com.example.web_demo.entity.User;
import com.example.web_demo.service.AuthenticationService;
import com.example.web_demo.service.JwtService;
import com.example.web_demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationService authService;
    private final JwtService jwtService;

    @Operation(summary = "Register new user", description = "Create a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "409", description = "Username already exists"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request.getUsername(), request.getEmail(), request.getPassword());

        AuthResponse response = AuthResponse.successWithToken(
                "User registered successfully",
                user.getId(),
                user.getUsername(),
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "User login", description = "Authenticate user and return user info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "401", description = "Login failed, wrong username or password"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        Optional<User> userOpt = authService.login(request.getUsername(), request.getPassword());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            AuthResponse response = AuthResponse.successWithToken(
                    "User logged in successfully",
                    user.getId(),
                    user.getUsername(),
                    jwtService.generateAccessToken(user),
                    jwtService.generateRefreshToken(user));

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        ApiErrorResponse errorRes = ApiErrorResponse.of(
                "Login Error",
                "Login error, wrong username or password",
                HttpStatus.UNAUTHORIZED.value(),
                httpRequest.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorRes);
    }

    @Operation(summary = "Refresh token", description = "Generate refresh token for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refresh token generated"),
            @ApiResponse(responseCode = "401", description = "Invalid token"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request, HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");

        if (!jwtService.isValidRefreshToken(request.getRefreshToken(), username)) {
            ApiErrorResponse errorRes = ApiErrorResponse.of(
                    "Invalid token",
                    "Invalid token",
                    HttpStatus.UNAUTHORIZED.value(),
                    httpRequest.getRequestURI());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorRes);
        }

        Optional<User> userOpt = userService.getUserByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            AuthResponse response = AuthResponse.successWithToken(
                    "Refresh token generated",
                    user.getId(),
                    user.getUsername(),
                    jwtService.generateAccessToken(user),
                    jwtService.generateRefreshToken(user));

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        ApiErrorResponse errorRes = ApiErrorResponse.of(
                "Invalid token",
                "Invalid token",
                HttpStatus.UNAUTHORIZED.value(),
                httpRequest.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorRes);
    }

}
