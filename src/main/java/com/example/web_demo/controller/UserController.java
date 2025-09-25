package com.example.web_demo.controller;

import java.util.Date;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_demo.dto.request.EmailVerificationConfirmRequest;
import com.example.web_demo.dto.response.ApiErrorResponse;
import com.example.web_demo.dto.response.RequestEmailVeificationResponse;
import com.example.web_demo.dto.response.UserDetailResponse;
import com.example.web_demo.entity.User;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User APIs")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @Operation(summary = "User detail", description = "Get User detail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User detail"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/detail")
    public ResponseEntity<?> getUserDetail(HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");

        Optional<User> userOpt = userService.getUserByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserDetailResponse response = UserDetailResponse.of(user);
            return ResponseEntity.ok(response);
        }
        ApiErrorResponse errorRes = ApiErrorResponse.of(
                "User not found",
                "User not found",
                HttpStatus.NOT_FOUND.value(),
                httpRequest.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorRes);
    }

    @Operation(summary = "Request Email Veirification", description = "Request email verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request created"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/request-email-verification")
    public ResponseEntity<?> postMethodName(HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");

        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Integer fiveMinutesInMillis = 300000;
            String veriifcationToken = jwtService.generateToken(user,
                    new Date(System.currentTimeMillis() + fiveMinutesInMillis));
            return ResponseEntity.ok(RequestEmailVeificationResponse.of(veriifcationToken));
        }
        ApiErrorResponse errorRes = ApiErrorResponse.of(
                "User not found",
                "User not found",
                HttpStatus.NOT_FOUND.value(),
                httpRequest.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorRes);
    }

    @Operation(summary = "Confirm Email Veirification", description = "Conrim email verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request created"),
            @ApiResponse(responseCode = "403", description = "Token is not valid"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/confirm-email-verification")
    public ResponseEntity<?> confirmEmailVerification(@Valid @RequestBody EmailVerificationConfirmRequest request,
            HttpServletRequest httpRequest) {
        String username = (String) httpRequest.getAttribute("username");
        String token = request.getVerificationToken();

        if (!jwtService.isValidToken(token, username)) {
            ApiErrorResponse errorRes = ApiErrorResponse.of(
                    "Invalid verification token",
                    "Invalid verification token",
                    HttpStatus.FORBIDDEN.value(),
                    httpRequest.getRequestURI());

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorRes);
        }

        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            userService.verifiedUserEmail(user);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }

        ApiErrorResponse errorRes = ApiErrorResponse.of(
                "User not found",
                "User not found",
                HttpStatus.NOT_FOUND.value(),
                httpRequest.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorRes);
    }
}
