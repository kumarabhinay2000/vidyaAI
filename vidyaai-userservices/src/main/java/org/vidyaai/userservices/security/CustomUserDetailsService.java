package org.vidyaai.userservices.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.userservices.entity.User;
import org.vidyaai.userservices.repository.UserRepository;
import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        log.debug("Loading user by email: {}", email);
        try {
            User user = userRepository.findByEmailAndIsDeletedFalse(email)
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "User not found with email: " + email
                    ));

            if (!user.isActive()) {
                log.warn("User account is not active: {}", email);
                throw new UsernameNotFoundException("User account is not active: " + email);
            }

            return buildUserDetails(user);
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error loading user by email: {}", email, e);
            throw new UsernameNotFoundException("Error loading user by email: " + email, e);
        }
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
        log.debug("Loading user by ID: {}", userId);

        try {
            User user = userRepository.findById(java.util.UUID.fromString(userId))
                    .orElseThrow(() -> new UsernameNotFoundException(
                            "User not found with Id: " + userId
                    ));

            if (!user.isActive()) {
                log.warn("User account is not active for Id: {}", userId);
                throw new UsernameNotFoundException("User account is not active " + userId);
            }
            return buildUserDetails(user);

        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error loading user by ID: {}", userId, e);
            throw new UsernameNotFoundException("Error loading user: " + userId, e);
        }
    }

    private UserDetails buildUserDetails(User user) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().getValue())
        );

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isActive())
                .build();
    }
}