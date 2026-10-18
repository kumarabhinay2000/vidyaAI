package org.vidyaai.videoservices.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.vidyaai.videoservices.exception.AccessDeniedException;

import java.util.UUID;

@Component
@Slf4j
public class SecurityUtils {

    public UUID getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                throw new AccessDeniedException("User is not authenticated");
            }
            Object principal = auth.getPrincipal();
            if (principal instanceof String username) {
                try {
                    return UUID.fromString(username);
                } catch (IllegalArgumentException e) {
                    log.warn("Principal username is not UUID: {}", username);
                    return null;
                }
            }
            return null;
        } catch (AccessDeniedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting current user ID: {}", e.getMessage(), e);
            throw new AccessDeniedException("Failed to get current user");
        }
    }

    public String getCurrentUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null ? auth.getName() : null;
        } catch (Exception e) {
            log.warn("Error getting current username: {}", e.getMessage());
            return null;
        }
    }
}