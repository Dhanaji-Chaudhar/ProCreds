package com.procreds.repository;

import com.procreds.entity.User;
import com.procreds.entity.UserPlatformPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPlatformPermissionRepository extends JpaRepository<UserPlatformPermission, Long> {
    
    Optional<UserPlatformPermission> findByUserAndPlatform(User user, String platform);
    
    List<UserPlatformPermission> findByUser(User user);
    
    List<UserPlatformPermission> findByPlatform(String platform);
    
    List<UserPlatformPermission> findByUserAndEnabled(User user, boolean enabled);
    
    List<UserPlatformPermission> findByPlatformAndEnabled(String platform, boolean enabled);
    
    @Query("SELECT p FROM UserPlatformPermission p WHERE p.user.id = :userId AND p.enabled = true")
    List<UserPlatformPermission> findEnabledPermissionsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT p FROM UserPlatformPermission p WHERE p.platform = :platform AND p.enabled = true")
    List<UserPlatformPermission> findEnabledPermissionsByPlatform(@Param("platform") String platform);
    
    @Query("SELECT p.platform FROM UserPlatformPermission p WHERE p.user.id = :userId AND p.enabled = true")
    List<String> findEnabledPlatformsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(p) FROM UserPlatformPermission p WHERE p.platform = :platform AND p.enabled = true")
    long countEnabledUsersByPlatform(@Param("platform") String platform);
    
    @Query("SELECT COUNT(p) FROM UserPlatformPermission p WHERE p.user.id = :userId AND p.enabled = true")
    long countEnabledPlatformsByUserId(@Param("userId") Long userId);
    
    boolean existsByUserAndPlatform(User user, String platform);
    
    void deleteByUserAndPlatform(User user, String platform);
    
    void deleteByUser(User user);
}

