package org.vidyaai.userservices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.vidyaai.userservices.entity.User;
import org.vidyaai.userservices.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Optional<User> findByIdAndIsDeletedFalse(UUID id);

    boolean existsByEmail(String email);
    boolean existsByEmailAndIsDeletedFalse(String email);

    List<User> findAllByStatusAndIsDeletedFalse(UserStatus status);

    List<User> findAllByRoleAndIsDeletedFalse(String role);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND u.status = :status")
    List<User> findActiveUserByStatus(@Param("status") UserStatus status);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND u.lastLogin < :date")
    List<User> findInactiveUsersBefore(@Param("date") LocalDateTime date);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false AND u.email LIKE %:domain%")
    List<User> findUsersByEmailDomain(@Param("domain") String domain);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status, u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int updateUserStatus(@Param("userId") UUID userId, @Param("status") UserStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isDeleted = true, u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int softDeleteUser(@Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLogin = :lastLogin WHERE u.id = :userId")
    int updateLastLogin(@Param("userId") UUID userId, @Param("lastLogin") LocalDateTime lastLogin);

    long countByIsDeletedFalse();
    long countByStatusAndIsDeletedFalse(UserStatus status);

    @Query("SELECT COUNT(u) FROM User u WHERE u.isDeleted = false AND u.createdAt >= :startDate AND u.createdAt <= :endDate")
    long countNewUsersBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}