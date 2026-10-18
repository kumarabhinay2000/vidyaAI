package org.vidyaai.userservices.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vidyaai.userservices.entity.User;
import org.vidyaai.userservices.exception.AuthenticationException;
import org.vidyaai.userservices.model.constants.AppConstants;
import org.vidyaai.userservices.service.JwtService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final SecretKey signingKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtServiceImpl(
            @Value("${spring.security.jwt.secret}") String secret,
            @Value("${spring.security.jwt.expiration}") long accessTokenExpiration,
            @Value("${spring.security.jwt.refresh-expiration}") long refreshTokenExpiration) {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
            this.accessTokenExpiration = accessTokenExpiration;
            this.refreshTokenExpiration = refreshTokenExpiration;
            log.info("JwtService initialized with access expiration: {}ms, refresh expiration: {}ms",
                    accessTokenExpiration, refreshTokenExpiration);
        } catch (Exception e) {
            log.error("Error initializing JwtService: {}", e.getMessage(), e);
            throw new AuthenticationException("Failed to initialize JWT service", e);
        }
    }

    @Override
    public String generateAccessToken(User user) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put(AppConstants.USER_ID_CLAIM, user.getId().toString());
            claims.put(AppConstants.EMAIL_CLAIM, user.getEmail());
            claims.put(AppConstants.ROLE_CLAIM, user.getRole().name());
            claims.put(AppConstants.PLAN_CLAIM, user.getPlanType().name());

            return buildToken(claims, user.getEmail(), accessTokenExpiration);
        } catch (Exception e) {
            log.error("Error generating access token for user: {}", user.getEmail(), e);
            throw new AuthenticationException("Failed to generate access token", e);
        }
    }

    @Override
    public String generateRefreshToken(User user) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put(AppConstants.USER_ID_CLAIM, user.getId().toString());

            return buildToken(claims, user.getEmail(), refreshTokenExpiration);
        } catch (Exception e) {
            log.error("Error generating refresh token for user: {}", user.getEmail(), e);
            throw new AuthenticationException("Failed to generate refresh token", e);
        }
    }

    @Override
    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            throw new AuthenticationException("Failed to extract username from token", e);
        }
    }

    @Override
    public UUID extractUserId(String token) {
        try {
            String userId = extractAllClaims(token).get(AppConstants.USER_ID_CLAIM, String.class);
            return UUID.fromString(userId);
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage());
            throw new AuthenticationException("Failed to extract user ID from token", e);
        }
    }

    @Override
    public String extractRole(String token) {
        try {
            return extractAllClaims(token).get(AppConstants.ROLE_CLAIM, String.class);
        } catch (Exception e) {
            log.error("Error extracting role from token: {}", e.getMessage());
            throw new AuthenticationException("Failed to extract role from token", e);
        }
    }

    @Override
    public String extractPlan(String token) {
        try {
            return extractAllClaims(token).get(AppConstants.PLAN_CLAIM, String.class);
        } catch (Exception e) {
            log.error("Error extracting plan from token: {}", e.getMessage());
            throw new AuthenticationException("Failed to extract plan from token", e);
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        } catch (SecurityException e) {
            log.warn("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    @Override
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    @Override
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    private String buildToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
}