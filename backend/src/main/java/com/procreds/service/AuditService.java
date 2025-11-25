package com.procreds.service;

import com.procreds.entity.AuditLog;
import com.procreds.entity.User;
import com.procreds.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    // Authentication Events
    public void logLogin(User user, boolean success, String details, String ipAddress, String userAgent) {
        logAction(user, AuditLog.LOGIN, "auth", null, null, details, ipAddress, userAgent, success);
    }

    public void logLogout(User user, String ipAddress, String userAgent) {
        logAction(user, AuditLog.LOGOUT, "auth", null, null, "User logged out", ipAddress, userAgent, true);
    }

    public void logPasswordChange(User user, boolean success, String details, String ipAddress, String userAgent) {
        logAction(user, AuditLog.PASSWORD_CHANGE, "auth", null, null, details, ipAddress, userAgent, success);
    }

    public void logPasswordReset(User user, boolean success, String details, String ipAddress, String userAgent) {
        logAction(user, AuditLog.PASSWORD_RESET, "auth", null, null, details, ipAddress, userAgent, success);
    }

    // User Management Events
    public void logUserCreate(User createdBy, User targetUser) {
        logAction(createdBy, AuditLog.USER_CREATE, "user", targetUser.getId().toString(), null,
            "Created user: " + targetUser.getUsername(), null, null, true);
    }

    public void logUserUpdate(User updatedBy, User targetUser) {
        logAction(updatedBy, AuditLog.USER_UPDATE, "user", targetUser.getId().toString(), null,
            "Updated user: " + targetUser.getUsername(), null, null, true);
    }

    public void logUserDelete(User deletedBy, User targetUser) {
        logAction(deletedBy, AuditLog.USER_DELETE, "user", targetUser.getId().toString(), null,
            "Deleted user: " + targetUser.getUsername(), null, null, true);
    }

    public void logUserEnable(User enabledBy, User targetUser) {
        logAction(enabledBy, AuditLog.USER_ENABLE, "user", targetUser.getId().toString(), null,
            "Enabled user: " + targetUser.getUsername(), null, null, true);
    }

    public void logUserDisable(User disabledBy, User targetUser) {
        logAction(disabledBy, AuditLog.USER_DISABLE, "user", targetUser.getId().toString(), null,
            "Disabled user: " + targetUser.getUsername(), null, null, true);
    }

    // Permission Events
    public void logPermissionGrant(User grantedBy, User targetUser, String platform, String permissionLevel) {
        logAction(grantedBy, AuditLog.PERMISSION_GRANT, "permission", targetUser.getId().toString(), platform,
            String.format("Granted %s permission to user %s for platform %s", permissionLevel, targetUser.getUsername(), platform),
            null, null, true);
    }

    public void logPermissionRevoke(User revokedBy, User targetUser, String platform) {
        logAction(revokedBy, AuditLog.PERMISSION_REVOKE, "permission", targetUser.getId().toString(), platform,
            String.format("Revoked permission from user %s for platform %s", targetUser.getUsername(), platform),
            null, null, true);
    }

    // Configuration Events
    public void logConfigCreate(User user, String platform, String configId, String ipAddress, String userAgent) {
        logAction(user, AuditLog.CONFIG_CREATE, "config", configId, platform,
            "Created configuration for platform: " + platform, ipAddress, userAgent, true);
    }

    public void logConfigUpdate(User user, String platform, String configId, String ipAddress, String userAgent) {
        logAction(user, AuditLog.CONFIG_UPDATE, "config", configId, platform,
            "Updated configuration for platform: " + platform, ipAddress, userAgent, true);
    }

    public void logConfigDelete(User user, String platform, String configId, String ipAddress, String userAgent) {
        logAction(user, AuditLog.CONFIG_DELETE, "config", configId, platform,
            "Deleted configuration for platform: " + platform, ipAddress, userAgent, true);
    }

    public void logConfigView(User user, String platform, String configId, String ipAddress, String userAgent) {
        logAction(user, AuditLog.CONFIG_VIEW, "config", configId, platform,
            "Viewed configuration for platform: " + platform, ipAddress, userAgent, true);
    }

    public void logTestConnection(User user, String platform, String configId, boolean success, 
                                String details, String ipAddress, String userAgent) {
        logAction(user, AuditLog.TEST_CONNECTION, "config", configId, platform,
            "Test connection for platform " + platform + ": " + details, ipAddress, userAgent, success);
    }

    // Generic Action Logging
    public void logAction(User user, String action, String resourceType, String resourceId, String platform,
                         String details, String ipAddress, String userAgent, boolean success) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUser(user);
            auditLog.setAction(action);
            auditLog.setResourceType(resourceType);
            auditLog.setResourceId(resourceId);
            auditLog.setPlatform(platform);
            auditLog.setDetails(details);
            auditLog.setIpAddress(ipAddress);
            auditLog.setUserAgent(userAgent);
            auditLog.setResult(success ? AuditLog.ActionResult.SUCCESS : AuditLog.ActionResult.FAILURE);
            auditLog.setCreatedAt(LocalDateTime.now());

            auditLogRepository.save(auditLog);
            
            log.debug("Audit log created: {} by user {} - {}", action, user.getUsername(), details);
            
        } catch (Exception ex) {
            log.error("Failed to create audit log for action: {} by user: {}", action, user.getUsername(), ex);
        }
    }

    public void logUnauthorizedAction(String username, String action, String resourceType, String resourceId, 
                                    String platform, String details, String ipAddress, String userAgent) {
        try {
            AuditLog auditLog = new AuditLog();
            // For unauthorized actions, we might not have a User entity
            auditLog.setUser(null);
            auditLog.setAction(action);
            auditLog.setResourceType(resourceType);
            auditLog.setResourceId(resourceId);
            auditLog.setPlatform(platform);
            auditLog.setDetails("Unauthorized attempt by " + username + ": " + details);
            auditLog.setIpAddress(ipAddress);
            auditLog.setUserAgent(userAgent);
            auditLog.setResult(AuditLog.ActionResult.UNAUTHORIZED);
            auditLog.setCreatedAt(LocalDateTime.now());

            auditLogRepository.save(auditLog);
            
            log.warn("Unauthorized action logged: {} by {} - {}", action, username, details);
            
        } catch (Exception ex) {
            log.error("Failed to create unauthorized audit log for action: {} by user: {}", action, username, ex);
        }
    }

    public void logForbiddenAction(User user, String action, String resourceType, String resourceId, 
                                 String platform, String details, String ipAddress, String userAgent) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUser(user);
            auditLog.setAction(action);
            auditLog.setResourceType(resourceType);
            auditLog.setResourceId(resourceId);
            auditLog.setPlatform(platform);
            auditLog.setDetails("Forbidden: " + details);
            auditLog.setIpAddress(ipAddress);
            auditLog.setUserAgent(userAgent);
            auditLog.setResult(AuditLog.ActionResult.FORBIDDEN);
            auditLog.setCreatedAt(LocalDateTime.now());

            auditLogRepository.save(auditLog);
            
            log.warn("Forbidden action logged: {} by user {} - {}", action, user.getUsername(), details);
            
        } catch (Exception ex) {
            log.error("Failed to create forbidden audit log for action: {} by user: {}", action, user.getUsername(), ex);
        }
    }

    // Query Methods
    @Transactional(readOnly = true)
    public Page<AuditLog> findAuditLogsByUser(User user, Pageable pageable) {
        return auditLogRepository.findByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findAuditLogsByAction(String action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findAuditLogsByPlatform(String platform, Pageable pageable) {
        return auditLogRepository.findByPlatform(platform, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findAuditLogsByResult(AuditLog.ActionResult result, Pageable pageable) {
        return auditLogRepository.findByResult(result, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return auditLogRepository.findByDateRange(startDate, endDate, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findAuditLogsByUserAndDateRange(Long userId, LocalDateTime startDate, 
                                                         LocalDateTime endDate, Pageable pageable) {
        return auditLogRepository.findByUserAndDateRange(userId, startDate, endDate, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findAuditLogsByPlatformAndDateRange(String platform, LocalDateTime startDate, 
                                                             LocalDateTime endDate, Pageable pageable) {
        return auditLogRepository.findByPlatformAndDateRange(platform, startDate, endDate, pageable);
    }

    // Statistics Methods
    @Transactional(readOnly = true)
    public long countActionsSince(String action, LocalDateTime since) {
        return auditLogRepository.countByActionSince(action, since);
    }

    @Transactional(readOnly = true)
    public long countFailuresSince(LocalDateTime since) {
        return auditLogRepository.countByResultSince(AuditLog.ActionResult.FAILURE, since);
    }

    @Transactional(readOnly = true)
    public long countUnauthorizedAttemptsSince(LocalDateTime since) {
        return auditLogRepository.countByResultSince(AuditLog.ActionResult.UNAUTHORIZED, since);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getPlatformActivitySince(LocalDateTime since) {
        return auditLogRepository.countByPlatformSince(since);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getActionStatisticsSince(LocalDateTime since) {
        return auditLogRepository.countByActionSince(since);
    }

    // Security Monitoring
    @Transactional(readOnly = true)
    public long getFailedLoginAttemptsSince(LocalDateTime since) {
        return countActionsSince(AuditLog.LOGIN, since) - countSuccessfulLoginsSince(since);
    }

    @Transactional(readOnly = true)
    public long countSuccessfulLoginsSince(LocalDateTime since) {
        // This would require a more complex query to count only successful logins
        // For now, we'll implement a simple version
        return auditLogRepository.countByActionSince(AuditLog.LOGIN, since);
    }

    @Transactional(readOnly = true)
    public boolean hasRecentSuspiciousActivity(String ipAddress, LocalDateTime since) {
        // This is a placeholder for more sophisticated suspicious activity detection
        // You could implement logic to detect patterns like:
        // - Multiple failed logins from same IP
        // - Rapid successive login attempts
        // - Access to multiple platforms in short time
        // - Unusual access patterns
        return false;
    }
}

