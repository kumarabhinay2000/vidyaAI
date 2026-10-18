package org.vidyaai.userservices.service;

import org.vidyaai.userservices.dto.request.ChangePasswordRequest;
import org.vidyaai.userservices.dto.request.LoginRequest;
import org.vidyaai.userservices.dto.request.RegisterRequest;
import org.vidyaai.userservices.dto.request.UpdateProfileRequest;
import org.vidyaai.userservices.dto.response.AuthResponse;
import org.vidyaai.userservices.dto.response.UserResponse;
import org.vidyaai.userservices.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    AuthResponse register(RegisterRequest registerRequest);

    AuthResponse login(LoginRequest loginRequest, String ipAddress, String userAgent);

    AuthResponse refreshToken(String refreshToken);

    void logout(String refreshToken);
    void logoutAllDevices(UUID userId);
    UserResponse getUserById(UUID userId);
    UserResponse getUserByEmail(String email);
    UserResponse updateProfile(UUID userId, UpdateProfileRequest updateRequest);
    void changePassword(UUID userId, ChangePasswordRequest changePasswordRequest);
    void deleteUser(UUID userId);
    List<UserResponse> getAllUsers();
    User getEntityById(UUID userId);
    User getEntityByEmail(String email);



}