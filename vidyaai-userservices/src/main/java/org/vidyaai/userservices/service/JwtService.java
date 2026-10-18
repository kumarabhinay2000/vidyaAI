package org.vidyaai.userservices.service;

import org.vidyaai.userservices.entity.User;
import java.util.UUID;

public interface JwtService {

    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String extractUsername(String token);
    UUID extractUserId(String token);
    String extractRole(String token);
    String extractPlan(String token);
    boolean validateToken(String token);
    boolean isTokenExpired(String token);
    long getAccessTokenExpiration();
    long getRefreshTokenExpiration();

}