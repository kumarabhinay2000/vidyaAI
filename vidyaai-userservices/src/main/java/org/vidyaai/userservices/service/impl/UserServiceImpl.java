package org.vidyaai.userservices.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.vidyaai.userservices.dto.request.ChangePasswordRequest;
import org.vidyaai.userservices.dto.request.LoginRequest;
import org.vidyaai.userservices.dto.request.RegisterRequest;
import org.vidyaai.userservices.dto.request.UpdateProfileRequest;
import org.vidyaai.userservices.dto.response.AuthResponse;
import org.vidyaai.userservices.dto.response.UserResponse;
import org.vidyaai.userservices.entity.RefreshToken;
import org.vidyaai.userservices.entity.Subscription;
import org.vidyaai.userservices.entity.User;
import org.vidyaai.userservices.entity.UserActivity;
import org.vidyaai.userservices.event.UserLoginEvent;
import org.vidyaai.userservices.event.UserRegisteredEvent;
import org.vidyaai.userservices.event.UserUpdatedEvent;
import org.vidyaai.userservices.exception.AuthenticationException;
import org.vidyaai.userservices.exception.DuplicateResourceException;
import org.vidyaai.userservices.exception.InvalidRequestException;
import org.vidyaai.userservices.exception.ResourceNotFoundException;
import org.vidyaai.userservices.model.constants.AppConstants;
import org.vidyaai.userservices.enums.PlanType;
import org.vidyaai.userservices.enums.Role;
import org.vidyaai.userservices.enums.UserStatus;
import org.vidyaai.userservices.repository.RefreshTokenRepository;
import org.vidyaai.userservices.repository.SubscriptionRepository;
import org.vidyaai.userservices.repository.UserActivityRepository;
import org.vidyaai.userservices.repository.UserRepository;
import org.vidyaai.userservices.service.JwtService;
import org.vidyaai.userservices.service.KafkaProducerService;
import org.vidyaai.userservices.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserActivityRepository userActivityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final KafkaProducerService kafkaProducerService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Registering new user with email: {}", registerRequest.getEmail());
        try {
            if (userRepository.existsByEmailAndIsDeletedFalse(registerRequest.getEmail())) {
                log.warn("Registration failed - email already exists: {}", registerRequest.getEmail());
                throw new DuplicateResourceException("User", "email", registerRequest.getEmail(),
                        AppConstants.ERROR_EMAIL_EXISTS);
            }

            User newUser = User.builder()
                    .email(registerRequest.getEmail().toLowerCase().trim())
                    .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                    .firstName(registerRequest.getFirstName().trim())
                    .lastName(StringUtils.hasText(registerRequest.getLastName())
                            ? registerRequest.getLastName().trim() : null)
                    .role(Role.STUDENT)
                    .status(UserStatus.ACTIVE)
                    .planType(PlanType.FREE)
                    .emailVerified(false)
                    .isDeleted(false)
                    .build();

            User savedUser = userRepository.save(newUser);
            log.info("User saved successfully with ID: {}", savedUser.getId());

            Subscription subscription = Subscription.builder()
                    .userId(savedUser.getId())
                    .planType(PlanType.FREE)
                    .startDate(LocalDateTime.now())
                    .isActive(true)
                    .videoLimit(5)
                    .videosUsed(0)
                    .build();
            subscriptionRepository.save(subscription);
            log.info("Free subscription created for user: {}", savedUser.getId());

            saveUserActivity(savedUser.getId(), "USER_REGISTERED",
                    "User registered with email: " + savedUser.getEmail());

            UserRegisteredEvent event = UserRegisteredEvent.builder()
                    .userId(savedUser.getId())
                    .email(savedUser.getEmail())
                    .firstName(savedUser.getFirstName())
                    .lastName(savedUser.getLastName())
                    .role(savedUser.getRole().name())
                    .registeredAt(LocalDateTime.now())
                    .build();
            kafkaProducerService.publishUserRegisteredEvent(event);

            String accessToken = jwtService.generateAccessToken(savedUser);
            String refreshToken = jwtService.generateRefreshToken(savedUser);
            saveRefreshToken(savedUser.getId(), refreshToken);

            log.info("User registered successfully: {}", savedUser.getId());
            return buildAuthResponse(savedUser, accessToken, refreshToken);

        } catch (DuplicateResourceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during user registration: {}", e.getMessage(), e);
            throw new AuthenticationException("Registration failed. Please try again.", e);
        }
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest, String ipAddress, String userAgent) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            User user = userRepository.findByEmailAndIsDeletedFalse(loginRequest.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email",
                            loginRequest.getEmail(), AppConstants.ERROR_USER_NOT_FOUND));

            if (!user.isActive()) {
                log.warn("Login failed - account not active: {}", user.getEmail());
                throw new AuthenticationException("Account is not active. Please contact support.",
                        "AUTH_008");
            }

            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            saveUserActivity(user.getId(), "USER_LOGIN",
                    "User logged in from IP: " + ipAddress);

            UserLoginEvent event = UserLoginEvent.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .loginAt(LocalDateTime.now())
                    .build();
            kafkaProducerService.publishUserLoginEvent(event);

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            saveRefreshToken(user.getId(), refreshToken);

            log.info("User logged in successfully: {}", user.getId());
            return buildAuthResponse(user, accessToken, refreshToken);

        } catch (BadCredentialsException e) {
            log.warn("Login failed - bad credentials for email: {}", loginRequest.getEmail());
            throw new AuthenticationException("Invalid email or password", AppConstants.ERROR_INVALID_CREDENTIALS);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage(), e);
            throw new AuthenticationException("Login failed. Please try again.", e);
        }
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshTokenValue) {
        log.info("Refresh token request received");
        try {
            RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(refreshTokenValue)
                    .orElseThrow(() -> new AuthenticationException("Invalid refresh token",
                            AppConstants.ERROR_INVALID_REFRESH_TOKEN));

            if (refreshToken.isExpired()) {
                log.warn("Refresh token expired for user: {}", refreshToken.getUserId());
                throw new AuthenticationException("Refresh token expired", AppConstants.ERROR_TOKEN_EXPIRED);
            }

            User user = userRepository.findByIdAndIsDeletedFalse(refreshToken.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id",
                            refreshToken.getUserId(), AppConstants.ERROR_USER_NOT_FOUND));

            refreshToken.revoke();
            refreshTokenRepository.save(refreshToken);

            String newAccessToken = jwtService.generateAccessToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);
            saveRefreshToken(user.getId(), newRefreshToken);

            log.info("Token refreshed successfully for user: {}", user.getId());
            return buildAuthResponse(user, newAccessToken, newRefreshToken);

        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error during token refresh: {}", e.getMessage(), e);
            throw new AuthenticationException("Token refresh failed", e);
        }
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        log.info("Logout request received");
        try {
            refreshTokenRepository.findByToken(refreshTokenValue).ifPresent(token -> {
                token.revoke();
                refreshTokenRepository.save(token);
                log.info("Refresh token revoked for user: {}", token.getUserId());
            });
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage(), e);
            throw new AuthenticationException("Logout failed", e);
        }
    }

    @Override
    @Transactional
    public void logoutAllDevices(UUID userId) {
        log.info("Logout all devices requested for user: {}", userId);
        try {
            int revokedCount = refreshTokenRepository.revokeAllActiveTokensForUser(userId);
            log.info("Revoked {} active tokens for user: {}", revokedCount, userId);
        } catch (Exception e) {
            log.error("Error logging out all devices for user: {}", userId, e);
            throw new AuthenticationException("Logout all devices failed", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        log.debug("Fetching user by ID: {}", userId);
        try {
            User user = userRepository.findByIdAndIsDeletedFalse(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId,
                            AppConstants.ERROR_USER_NOT_FOUND));
            return mapToUserResponse(user);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching user by ID: {}", userId, e);
            throw new InvalidRequestException("Failed to fetch user details");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        try {
            User user = userRepository.findByEmailAndIsDeletedFalse(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email,
                            AppConstants.ERROR_USER_NOT_FOUND));
            return mapToUserResponse(user);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching user by email: {}", email, e);
            throw new InvalidRequestException("Failed to fetch user details");
        }
    }

    @Override
    @Transactional
    public UserResponse updateProfile(UUID userId, UpdateProfileRequest updateRequest) {
        log.info("Updating profile for user: {}", userId);
        try {
            User user = userRepository.findByIdAndIsDeletedFalse(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId,
                            AppConstants.ERROR_USER_NOT_FOUND));

            StringBuilder updatedFields = new StringBuilder();

            if (StringUtils.hasText(updateRequest.getFirstName())) {
                user.setFirstName(updateRequest.getFirstName().trim());
                updatedFields.append("firstName,");
            }

            if (StringUtils.hasText(updateRequest.getLastName())) {
                user.setLastName(updateRequest.getLastName().trim());
                updatedFields.append("lastName,");
            }

            if (StringUtils.hasText(updateRequest.getProfilePicUrl())) {
                user.setProfilePicUrl(updateRequest.getProfilePicUrl().trim());
                updatedFields.append("profilePicUrl,");
            }

            if (updatedFields.length() == 0) {
                log.warn("No fields to update for user: {}", userId);
                throw new InvalidRequestException("No fields provided for update");
            }

            User updatedUser = userRepository.save(user);
            log.info("Profile updated for user: {}", userId);

            saveUserActivity(userId, "PROFILE_UPDATED",
                    "Updated fields: " + updatedFields.toString());

            UserUpdatedEvent event = UserUpdatedEvent.builder()
                    .userId(userId)
                    .email(updatedUser.getEmail())
                    .updatedField(updatedFields.toString())
                    .updatedAt(LocalDateTime.now())
                    .build();
            kafkaProducerService.publishUserUpdatedEvent(event);

            return mapToUserResponse(updatedUser);

        } catch (ResourceNotFoundException | InvalidRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating profile for user: {}", userId, e);
            throw new InvalidRequestException("Failed to update profile");
        }
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest changePasswordRequest) {
        log.info("Change password request for user: {}", userId);
        try {
            User user = userRepository.findByIdAndIsDeletedFalse(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId,
                            AppConstants.ERROR_USER_NOT_FOUND));

            if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPasswordHash())) {
                log.warn("Current password mismatch for user: {}", userId);
                throw new AuthenticationException("Current password is incorrect",
                        AppConstants.ERROR_INVALID_CREDENTIALS);
            }

            if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), user.getPasswordHash())) {
                log.warn("New password same as current for user: {}", userId);
                throw new InvalidRequestException("New password must be different from current password");
            }

            user.setPasswordHash(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            userRepository.save(user);

            refreshTokenRepository.revokeAllActiveTokensForUser(userId);
            log.info("All active tokens revoked after password change for user: {}", userId);

            saveUserActivity(userId, "PASSWORD_CHANGED",
                    "Password changed successfully");

            log.info("Password changed successfully for user: {}", userId);

        } catch (ResourceNotFoundException | AuthenticationException | InvalidRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error changing password for user: {}", userId, e);
            throw new InvalidRequestException("Failed to change password");
        }
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        log.info("Delete user request for user: {}", userId);
        try {
            User user = userRepository.findByIdAndIsDeletedFalse(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId,
                            AppConstants.ERROR_USER_NOT_FOUND));

            user.setIsDeleted(true);
            user.setDeletedAt(LocalDateTime.now());
            user.setStatus(UserStatus.DELETED);
            userRepository.save(user);

            refreshTokenRepository.revokeAllActiveTokensForUser(userId);
            subscriptionRepository.deactivateAllSubscriptionsForUser(userId);

            saveUserActivity(userId, "USER_DELETED",
                    "User account deleted");

            log.info("User soft deleted successfully: {}", userId);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting user: {}", userId, e);
            throw new InvalidRequestException("Failed to delete user");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");
        try {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .filter(user -> !Boolean.TRUE.equals(user.getIsDeleted()))
                    .map(this::mapToUserResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all users", e);
            throw new InvalidRequestException("Failed to fetch users");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getEntityById(UUID userId) {
        log.debug("Fetching user entity by ID: {}", userId);
        try {
            return userRepository.findByIdAndIsDeletedFalse(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId,
                            AppConstants.ERROR_USER_NOT_FOUND));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching user entity by ID: {}", userId, e);
            throw new InvalidRequestException("Failed to fetch user entity");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getEntityByEmail(String email) {
        log.debug("Fetching user entity by email: {}", email);
        try {
            return userRepository.findByEmailAndIsDeletedFalse(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "email", email,
                            AppConstants.ERROR_USER_NOT_FOUND));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching user entity by email: {}", email, e);
            throw new InvalidRequestException("Failed to fetch user entity");
        }
    }

    private void saveRefreshToken(UUID userId, String token) {
        try {
            RefreshToken refreshToken = RefreshToken.builder()
                    .userId(userId)
                    .token(token)
                    .expiresAt(LocalDateTime.now().plusSeconds(
                            jwtService.getRefreshTokenExpiration() / 1000))
                    .revoked(false)
                    .build();
            refreshTokenRepository.save(refreshToken);
            log.debug("Refresh token saved for user: {}", userId);
        } catch (Exception e) {
            log.error("Error saving refresh token for user: {}", userId, e);
            throw new AuthenticationException("Failed to save refresh token", e);
        }
    }

    private void saveUserActivity(UUID userId, String actionType, String metadata) {
        try {
            UserActivity activity = UserActivity.builder()
                    .userId(userId)
                    .actionType(actionType)
                    .metadata("{\"info\":\"" + metadata + "\"}")
                    .build();
            userActivityRepository.save(activity);
            log.debug("User activity saved: {} for user: {}", actionType, userId);
        } catch (Exception e) {
            log.error("Error saving user activity for user: {}", userId, e);
        }
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType(AppConstants.TOKEN_TYPE)
                .expiresIn(jwtService.getAccessTokenExpiration() / 1000)
                .user(mapToUserResponse(user))
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus())
                .planType(user.getPlanType())
                .profilePicUrl(user.getProfilePicUrl())
                .emailVerified(user.getEmailVerified())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}