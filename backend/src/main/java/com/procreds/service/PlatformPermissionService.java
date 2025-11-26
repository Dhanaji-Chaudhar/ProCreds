package com.procreds.service;

import com.procreds.entity.User;
import com.procreds.entity.UserPlatformPermission;
import com.procreds.exception.BadRequestException;
import com.procreds.exception.ResourceNotFoundException;
import com.procreds.repository.UserPlatformPermissionRepository;
import com.procreds.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlatformPermissionService {

    private final UserPlatformPermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public List<UserPlatformPermission> getUserPermissions(Long userId) {
        log.info("Retrieving permissions for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return permissionRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public Page<UserPlatformPermission> getPlatformUsers(String platform, Boolean enabled, Pageable pageable) {
        log.info("Retrieving users for platform: {} with enabled filter: {}", platform, enabled);
        
        if (enabled != null) {
            return permissionRepository.findByPlatformAndEnabled(platform, enabled, pageable);
        } else {
            return permissionRepository.findByPlatform(platform, pageable);
        }
    }

    public UserPlatformPermission grantPermission(Long userId, String platform, 
                                                UserPlatformPermission.PermissionLevel permissionLevel,
                                                User grantedBy, String ipAddress, String userAgent) {
        log.info("Granting {} permission to user ID: {} for platform: {}", permissionLevel, userId, platform);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Check if permission already exists
        if (permissionRepository.existsByUserAndPlatform(user, platform)) {
            throw new BadRequestException("Permission already exists for user " + user.getUsername() + " on platform " + platform);
        }
        
        // Validate platform
        if (!isValidPlatform(platform)) {
            throw new BadRequestException("Invalid platform: " + platform);
        }
        
        // Create new permission
        UserPlatformPermission permission = new UserPlatformPermission();
        permission.setUser(user);
        permission.setPlatform(platform);
        permission.setPermissionLevel(permissionLevel);
        permission.setEnabled(true);
        permission.setGrantedBy(grantedBy);
        
        permission = permissionRepository.save(permission);
        
        // Log audit event
        auditService.logPermissionGrant(grantedBy, user, platform, permissionLevel.name());
        
        // Send notification email
        try {
            emailService.sendPlatformAccessEmail(
                user.getEmail(), 
                user.getFullName(), 
                platform, 
                permissionLevel.getDisplayName(), 
                true
            );
        } catch (Exception ex) {
            log.warn("Failed to send platform access email to user: {}", user.getEmail(), ex);
        }
        
        log.info("Successfully granted {} permission to user: {} for platform: {}", 
            permissionLevel, user.getUsername(), platform);
        
        return permission;
    }

    public UserPlatformPermission updatePermission(Long userId, String platform, 
                                                 UserPlatformPermission.PermissionLevel permissionLevel,
                                                 boolean enabled, User updatedBy, 
                                                 String ipAddress, String userAgent) {
        log.info("Updating permission for user ID: {} on platform: {} to level: {} enabled: {}", 
            userId, platform, permissionLevel, enabled);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        UserPlatformPermission permission = permissionRepository.findByUserAndPlatform(user, platform)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Permission not found for user " + user.getUsername() + " on platform " + platform));
        
        // Store old values for audit
        UserPlatformPermission.PermissionLevel oldLevel = permission.getPermissionLevel();
        boolean oldEnabled = permission.isEnabled();
        
        // Update permission
        permission.setPermissionLevel(permissionLevel);
        permission.setEnabled(enabled);
        
        permission = permissionRepository.save(permission);
        
        // Log audit event
        String details = String.format("Updated permission from %s to %s, enabled: %s to %s", 
            oldLevel.name(), permissionLevel.name(), oldEnabled, enabled);
        auditService.logAction(updatedBy, "PERMISSION_UPDATE", "permission", 
            permission.getId().toString(), platform, details, ipAddress, userAgent, true);
        
        // Send notification email if permission was disabled/enabled
        if (oldEnabled != enabled) {
            try {
                emailService.sendPlatformAccessEmail(
                    user.getEmail(), 
                    user.getFullName(), 
                    platform, 
                    permissionLevel.getDisplayName(), 
                    enabled
                );
            } catch (Exception ex) {
                log.warn("Failed to send platform access email to user: {}", user.getEmail(), ex);
            }
        }
        
        log.info("Successfully updated permission for user: {} on platform: {}", user.getUsername(), platform);
        
        return permission;
    }

    public void revokePermission(Long userId, String platform, User revokedBy, 
                               String ipAddress, String userAgent) {
        log.info("Revoking permission for user ID: {} on platform: {}", userId, platform);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        UserPlatformPermission permission = permissionRepository.findByUserAndPlatform(user, platform)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Permission not found for user " + user.getUsername() + " on platform " + platform));
        
        // Delete permission
        permissionRepository.delete(permission);
        
        // Log audit event
        auditService.logPermissionRevoke(revokedBy, user, platform);
        
        // Send notification email
        try {
            emailService.sendPlatformAccessEmail(
                user.getEmail(), 
                user.getFullName(), 
                platform, 
                permission.getPermissionLevel().getDisplayName(), 
                false
            );
        } catch (Exception ex) {
            log.warn("Failed to send platform access email to user: {}", user.getEmail(), ex);
        }
        
        log.info("Successfully revoked permission for user: {} on platform: {}", user.getUsername(), platform);
    }

    @Transactional(readOnly = true)
    public boolean hasUserPermission(Long userId, String platform, UserPlatformPermission.PermissionLevel requiredLevel) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Admin users have access to all platforms
        if (user.hasRole("ADMIN")) {
            return true;
        }
        
        UserPlatformPermission permission = permissionRepository.findByUserAndPlatformAndEnabled(user, platform, true)
            .orElse(null);
        
        if (permission == null) {
            return false;
        }
        
        return hasRequiredPermissionLevel(permission.getPermissionLevel(), requiredLevel);
    }

    @Transactional(readOnly = true)
    public UserPlatformPermission.PermissionLevel getUserPermissionLevel(Long userId, String platform) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Admin users have ADMIN level access to all platforms
        if (user.hasRole("ADMIN")) {
            return UserPlatformPermission.PermissionLevel.ADMIN;
        }
        
        return permissionRepository.findByUserAndPlatformAndEnabled(user, platform, true)
            .map(UserPlatformPermission::getPermissionLevel)
            .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<String> getUserAccessiblePlatforms(Long userId) {
        return permissionRepository.findEnabledPlatformsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long countUsersWithPlatformAccess(String platform) {
        return permissionRepository.countEnabledUsersByPlatform(platform);
    }

    @Transactional(readOnly = true)
    public long countUserPlatforms(Long userId) {
        return permissionRepository.countEnabledPlatformsByUserId(userId);
    }

    private boolean hasRequiredPermissionLevel(UserPlatformPermission.PermissionLevel userLevel, 
                                             UserPlatformPermission.PermissionLevel requiredLevel) {
        // Permission hierarchy: ADMIN > READ_WRITE > READ_ONLY
        switch (requiredLevel) {
            case READ_ONLY:
                return true; // Any level can read
            case READ_WRITE:
                return userLevel == UserPlatformPermission.PermissionLevel.READ_WRITE || 
                       userLevel == UserPlatformPermission.PermissionLevel.ADMIN;
            case ADMIN:
                return userLevel == UserPlatformPermission.PermissionLevel.ADMIN;
            default:
                return false;
        }
    }

    private boolean isValidPlatform(String platform) {
        return List.of(
            UserPlatformPermission.GITHUB,
            UserPlatformPermission.BITBUCKET,
            UserPlatformPermission.GITLAB,
            UserPlatformPermission.JENKINS,
            UserPlatformPermission.JIRA,
            UserPlatformPermission.SONARQUBE,
            UserPlatformPermission.KUBERNETES,
            UserPlatformPermission.AWS_EKS,
            UserPlatformPermission.AZURE_AKS,
            UserPlatformPermission.GCP_GKE
        ).contains(platform);
    }
}
