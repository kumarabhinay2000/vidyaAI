package org.vidyaai.userservices.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.userservices.dto.request.LoginRequest;
import org.vidyaai.userservices.dto.request.RefreshTokenRequest;
import org.vidyaai.userservices.dto.request.RegisterRequest;
import org.vidyaai.userservices.dto.response.ApiResponse;
import org.vidyaai.userservices.dto.response.AuthResponse;
import org.vidyaai.userservices.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j

public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest
    ) {
        log.info("Received registeration request for email: {}", registerRequest.getEmail());

        try {

            AuthResponse response= userService.register(registerRequest);
            log.info("Registeration successfully for email: {}", registerRequest.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "User registered successfully"));
        } catch(Exception e) {
            log.error("Registration failed for email: {}", registerRequest.getEmail(), e);
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        log.info("Received login request for email: {}", loginRequest.getEmail());

        try {
            String ipAddress=getClientIp(request);
            String userAgent =request.getHeader("user-Agent");

            AuthResponse response= userService.login(loginRequest, ipAddress, userAgent);

            log.info("Login successful for email: {}", loginRequest.getEmail());
            return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
        } catch(Exception e) {
            log.error("Login failed or email: {}", loginRequest.getEmail(), e);
            throw e;
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Received refresh token request");

        try {
            AuthResponse response = userService.refreshToken(refreshTokenRequest.getRefreshToken());
            return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Received logout request");
        try {

            userService.logout(refreshTokenRequest.getRefreshToken());
            log.info("Logout successful");

            return ResponseEntity.ok(ApiResponse.success(null, "Logout successful"));
        }catch(Exception e) {
            log.error("Logout failed ", e);
            throw e;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        try {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }
            String xRealIp = request.getHeader("X-Real-Ip");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }
            return request.getRemoteAddr();
        } catch (Exception e) {
            log.error("Error extracting client Ip: {}", e.getMessage());
            return "Unknown";
        }
    }
}