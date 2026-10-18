package org.vidyaai.userservices.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.vidyaai.userservices.dto.request.ChangePasswordRequest;
import org.vidyaai.userservices.dto.request.UpdateProfileRequest;
import org.vidyaai.userservices.dto.response.ApiResponse;
import org.vidyaai.userservices.dto.response.UserResponse;
import org.vidyaai.userservices.entity.User;
import org.vidyaai.userservices.service.UserService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Fetching profile for user: {}", userDetails.getUsername());
        try {
            User user = userService.getEntityByEmail(userDetails.getUsername());
            UserResponse response = userService.getUserById(user.getId());

            return ResponseEntity.ok(ApiResponse.success(response, "Profile fetched successfully"));
        } catch (Exception e) {
            log.error("Error fetching profile for user: {}", userDetails.getUsername(), e);
            throw e;
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER') or #userId.toString()==authentication.principal.username")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable UUID userId) {
        log.info("Fetching user by ID: {}", userId);

        try {
            UserResponse response = userService.getUserById(userId);

            return ResponseEntity.ok(ApiResponse.success(response, "User fetched successfully"));
        } catch (Exception e) {
            log.error("Error fetching user by ID: {}", userId, e);
            throw e;
        }
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        log.info("Fetching user by email: {}", email);
        try {
            UserResponse response = userService.getUserByEmail(email);

            return ResponseEntity.ok(ApiResponse.success(response, "User fetched successfully"));
        } catch (Exception e) {
            log.error("Error fetching user by email: {}", email, e);
            throw e;
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest updateProfileRequest) {

        log.info("Updating profile for user: {}", userDetails.getUsername());
        try {
            User user = userService.getEntityByEmail(userDetails.getUsername());
            UserResponse response = userService.updateProfile(user.getId(), updateProfileRequest);

            return ResponseEntity.ok(ApiResponse.success(response, "Profile updated successfully"));
        } catch (Exception e) {
            log.error("Error updating profile for user: {}", userDetails.getUsername(), e);
            throw e;
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {

        log.info("Change password request for user: {}", userDetails.getUsername());

        try {
            User user = userService.getEntityByEmail(userDetails.getUsername());
            userService.changePassword(user.getId(), changePasswordRequest);

            return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
        } catch (Exception e) {
            log.error("Error changing password for user: {}", userDetails.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/logout-all-devices")
    public ResponseEntity<ApiResponse<Void>> logoutAllDevices(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Logout all devices request for user: {}", userDetails.getUsername());
        try {
            User user = userService.getEntityByEmail(userDetails.getUsername());
            userService.logoutAllDevices(user.getId());

            return ResponseEntity.ok(ApiResponse.success(null, "Logged out from all devices successfully"));
        } catch (Exception e) {
            log.error("Error logging out all devices for user: {}", userDetails.getUsername(), e);
            throw e;
        }
    }

    @DeleteMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Delete account request for user: {}", userDetails.getUsername());
        try {
            User user = userService.getEntityByEmail(userDetails.getUsername());
            userService.deleteUser(user.getId());

            return ResponseEntity.ok(ApiResponse.success(null, "Account deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting account for user: {}", userDetails.getUsername(), e);
            throw e;
        }
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        log.info("Fetching all users (admin)");
        try {
            List<UserResponse> users = userService.getAllUsers();

            return ResponseEntity.ok(ApiResponse.success(users, "User fetched successfully"));
        } catch (Exception e) {
            log.error("Error fetching all users", e);
            throw e;
        }
    }
}
