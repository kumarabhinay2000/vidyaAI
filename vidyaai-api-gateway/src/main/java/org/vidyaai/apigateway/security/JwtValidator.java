package org.vidyaai.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@Slf4j
public class JwtValidator {

    private final SecretKey signingKey;

    public JwtValidator(@Value("${gateway.security.jwt.secret}") String secret) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        log.info("JwtValidator initialized");
    }

    public Claims validateAndExtract(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            throw e;
        }
    }

    public boolean isValid(String token) {
        try {
            validateAndExtract(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}