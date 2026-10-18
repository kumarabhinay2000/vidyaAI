package org.vidyaai.aiservices.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.vidyaai.aiservices.exception.AuthenticationException;

import java.util.UUID;

@Component
@Slf4j
public class SecurityUtils {

    public UUID getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new AuthenticationException("User is not authenticated");
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                String username = userDetails.getUsername();
                try {
                    return UUID.fromString(username);
                } catch (IllegalArgumentException e) {
                    log.warn("Principal username is not UUID: {}", username);
                    return null;
                }
            }
            return null;
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting current user ID: {}", e.getMessage(), e);
            throw new AuthenticationException("Failed to get current user");
        }
    }

    public String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
            return authentication.getName();
        } catch (Exception e) {
            log.error("Error getting current username: {}", e.getMessage());
            return null;
        }
    }
}