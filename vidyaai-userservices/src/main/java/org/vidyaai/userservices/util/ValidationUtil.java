package org.vidyaai.userservices.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$"
    );

    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[a-zA-Z\\s]+$"
    );

    public boolean isValidEmail(String email) {
        try {
            if (email == null || email.isEmpty()) {
                return false;
            }
            return EMAIL_PATTERN.matcher(email).matches();
        } catch (Exception e) {
            log.error("Error validating email: {}", e.getMessage());
            return false;
        }
    }

    public boolean isValidPassword(String password) {
        try {
            if (password == null || password.length() < 8 || password.length() > 100) {
                return false;
            }
            return PASSWORD_PATTERN.matcher(password).matches();
        } catch (Exception e) {
            log.error("Error validating password: {}", e.getMessage());
            return false;
        }
    }

    public boolean isValidName(String name) {
        try {
            if (name == null || name.isEmpty()) {
                return false;
            }
            return NAME_PATTERN.matcher(name).matches();
        } catch (Exception e) {
            log.error("Error validating name: {}", e.getMessage());
            return false;
        }
    }

    public String sanitizeEmail(String email) {
        try {
            if (email == null) {
                return null;
            }
            return email.toLowerCase().trim();
        } catch (Exception e) {
            log.error("Error sanitizing email: {}", e.getMessage());
            return email;
        }
    }

    public String sanitizeName(String name) {
        try {
            if (name == null) {
                return null;
            }
            return name.trim();
        } catch (Exception e) {
            log.error("Error sanitizing name: {}", e.getMessage());
            return name;
        }
    }
}