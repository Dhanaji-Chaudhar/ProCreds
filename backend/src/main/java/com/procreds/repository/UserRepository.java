package com.procreds.repository;

import com.procreds.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPasswordResetToken(String token);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.enabled = true")
    List<User> findAllEnabledUsers();
    
    @Query("SELECT u FROM User u WHERE u.enabled = :enabled")
    Page<User> findByEnabled(@Param("enabled") boolean enabled, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> findBySearchTerm(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
    
    @Query("SELECT u FROM User u JOIN u.platformPermissions p WHERE p.platform = :platform AND p.enabled = true")
    List<User> findByPlatformAccess(@Param("platform") String platform);
    
    @Query("SELECT u FROM User u WHERE u.accountLockedUntil IS NOT NULL AND u.accountLockedUntil > :now")
    List<User> findLockedUsers(@Param("now") LocalDateTime now);
    
    @Query("SELECT u FROM User u WHERE u.passwordResetTokenExpiry IS NOT NULL AND u.passwordResetTokenExpiry < :now")
    List<User> findUsersWithExpiredResetTokens(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.enabled = true")
    long countEnabledUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.lastLogin > :since")
    long countActiveUsersSince(@Param("since") LocalDateTime since);
}

