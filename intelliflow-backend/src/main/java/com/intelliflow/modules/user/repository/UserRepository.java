package com.intelliflow.modules.user.repository;

import com.intelliflow.modules.user.domain.UserEntity;
import com.intelliflow.modules.user.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA Repository interface for UserEntity data access.
 * 
 * Provides automated query derivation methods and atomic JPQL mutation queries
 * for user authentication, account locking, and soft-delete lifecycle operations.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Finds user entity by email regardless of soft deletion state.
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Finds active (non-soft-deleted) user entity by email.
     */
    Optional<UserEntity> findByEmailAndDeletedAtIsNull(String email);

    /**
     * Finds active (non-soft-deleted) user entity by UUID.
     */
    Optional<UserEntity> findByIdAndDeletedAtIsNull(UUID id);

    /**
     * Checks if email exists among active non-deleted users.
     */
    boolean existsByEmailAndDeletedAtIsNull(String email);

    /**
     * Atomically increments failed login attempts counter.
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.failedLoginAttempts = u.failedLoginAttempts + 1 WHERE u.id = :userId")
    int incrementFailedLoginAttempts(@Param("userId") UUID userId);

    /**
     * Atomically locks account by setting status to LOCKED and setting lockout expiration timestamp.
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.status = :status, u.lockoutUntil = :lockoutUntil WHERE u.id = :userId")
    int lockAccount(
            @Param("userId") UUID userId,
            @Param("status") UserStatus status,
            @Param("lockoutUntil") Instant lockoutUntil
    );

    /**
     * Atomically resets failed login attempts and clears lockout timestamp on successful authentication.
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.failedLoginAttempts = 0, u.lockoutUntil = NULL WHERE u.id = :userId")
    int resetFailedLoginAttempts(@Param("userId") UUID userId);

    /**
     * Atomically updates user administrative status.
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.status = :status WHERE u.id = :userId")
    int updateUserStatus(
            @Param("userId") UUID userId,
            @Param("status") UserStatus status
    );

    /**
     * Atomically soft-deletes a user account by setting status to DELETED and populating deletedAt timestamp.
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.status = :status, u.deletedAt = :deletedAt WHERE u.id = :userId")
    int softDeleteUser(
            @Param("userId") UUID userId,
            @Param("status") UserStatus status,
            @Param("deletedAt") Instant deletedAt
    );
}
