package com.example.web_demo.component;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.web_demo.service.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new JwtException("Token is not in header");
        }

        String token = authHeader.substring(7);
        String username = jwtService.getUsernameFromToken(token);

        if (path.startsWith("/api/auth/refresh-token")) {
            request.setAttribute("username", username);
            request.setAttribute("token", token);
            PreAuthenticatedAuthenticationToken authToken = new PreAuthenticatedAuthenticationToken(username, token,
                    Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);
            return;
        }

        if (!jwtService.isValidAccessToken(token, username)) {
            throw new JwtException("Token is not valid");
        }

        request.setAttribute("username", username);
        request.setAttribute("token", token);

        PreAuthenticatedAuthenticationToken authToken = new PreAuthenticatedAuthenticationToken(username, token,
                Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/register") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/v3/api-docs") ||
                path.contains("swagger") ||
                path.contains("api-docs");
    }
}
