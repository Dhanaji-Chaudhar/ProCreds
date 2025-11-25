package com.procreds.controller;

import com.procreds.dto.ApiResponse;
import com.procreds.entity.User;
import com.procreds.entity.UserPlatformPermission;
import com.procreds.service.CustomUserDetailsService;
import com.procreds.service.PlatformPermissionService;
import com.procreds.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Platform Permissions", description = "Platform permission management endpoints")
public class PlatformPermissionController {

    private final PlatformPermissionService platformPermissionService;
    private final UserService userService;

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @Operation(summary = "Get user permissions", description = "Get all platform permissions for a user")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permissions retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<List<PlatformPermissionResponse>>> getUserPermissions(
            @PathVariable Long userId) {
        
        try {
            List<UserPlatformPermission> permissions = platformPermissionService.getUserPermissions(userId);
            List<PlatformPermissionResponse> response = permissions.stream()
                .map(this::convertToResponse)
                .toList();
            
            return ResponseEntity.ok(
                ApiResponse.success("Permissions retrieved successfully", response)
            );
            
        } catch (Exception ex) {
            log.error("Failed to retrieve permissions for user ID: {}", userId, ex);
            return ResponseEntity.status(404).body(
                ApiResponse.error("Failed to retrieve permissions: " + ex.getMessage())
            );
        }
    }

    @GetMapping("/platforms/{platform}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get platform users", description = "Get all users with access to a platform (Admin only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Platform users retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<PlatformUserResponse>>> getPlatformUsers(
            @PathVariable String platform,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "user.username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Boolean enabled) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<UserPlatformPermission> permissions = platformPermissionService.getPlatformUsers(
                platform, enabled, pageable);
            
            Page<PlatformUserResponse> response = permissions.map(this::convertToPlatformUserResponse);
            
            return ResponseEntity.ok(
                ApiResponse.success("Platform users retrieved successfully", response)
            );
            
        } catch (Exception ex) {
            log.error("Failed to retrieve users for platform: {}", platform, ex);
            return ResponseEntity.status(500).body(
                ApiResponse.error("Failed to retrieve platform users: " + ex.getMessage())
            );
        }
    }

    @PostMapping("/users/{userId}/platforms/{platform}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Grant platform permission", description = "Grant platform access to a user (Admin only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Permission granted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request or permission already exists"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<PlatformPermissionResponse>> grantPermission(
            @PathVariable Long userId,
            @PathVariable String platform,
            @Valid @RequestBody GrantPermissionRequest grantPermissionRequest,
            HttpServletRequest request) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetailsService.UserPrincipal userPrincipal = 
                (CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            User grantedBy = userService.findById(userPrincipal.getId());
            
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            UserPlatformPermission permission = platformPermissionService.grantPermission(
                userId, 
                platform, 
                UserPlatformPermission.PermissionLevel.valueOf(grantPermissionRequest.getPermissionLevel()),
                grantedBy,
                ipAddress,
                userAgent
            );
            
            PlatformPermissionResponse response = convertToResponse(permission);
            
            log.info("Permission granted: {} access to {} for user ID: {} by admin: {}", 
                grantPermissionRequest.getPermissionLevel(), platform, userId, grantedBy.getUsername());
            
            return ResponseEntity.status(201).body(
                ApiResponse.success("Permission granted successfully", response)
            );
            
        } catch (Exception ex) {
            log.error("Failed to grant permission for user ID: {} on platform: {}", userId, platform, ex);
            return ResponseEntity.status(400).body(
                ApiResponse.error("Failed to grant permission: " + ex.getMessage())
            );
        }
    }

    @PutMapping("/users/{userId}/platforms/{platform}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update platform permission", description = "Update platform permission level for a user (Admin only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permission updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Permission not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<PlatformPermissionResponse>> updatePermission(
            @PathVariable Long userId,
            @PathVariable String platform,
            @Valid @RequestBody UpdatePermissionRequest updatePermissionRequest,
            HttpServletRequest request) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetailsService.UserPrincipal userPrincipal = 
                (CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            User updatedBy = userService.findById(userPrincipal.getId());
            
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            UserPlatformPermission permission = platformPermissionService.updatePermission(
                userId, 
                platform, 
                UserPlatformPermission.PermissionLevel.valueOf(updatePermissionRequest.getPermissionLevel()),
                updatePermissionRequest.isEnabled(),
                updatedBy,
                ipAddress,
                userAgent
            );
            
            PlatformPermissionResponse response = convertToResponse(permission);
            
            log.info("Permission updated: {} access to {} for user ID: {} by admin: {}", 
                updatePermissionRequest.getPermissionLevel(), platform, userId, updatedBy.getUsername());
            
            return ResponseEntity.ok(
                ApiResponse.success("Permission updated successfully", response)
            );
            
        } catch (Exception ex) {
            log.error("Failed to update permission for user ID: {} on platform: {}", userId, platform, ex);
            return ResponseEntity.status(404).body(
                ApiResponse.error("Failed to update permission: " + ex.getMessage())
            );
        }
    }

    @DeleteMapping("/users/{userId}/platforms/{platform}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Revoke platform permission", description = "Revoke platform access from a user (Admin only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permission revoked successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Permission not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> revokePermission(
            @PathVariable Long userId,
            @PathVariable String platform,
            HttpServletRequest request) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetailsService.UserPrincipal userPrincipal = 
                (CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            User revokedBy = userService.findById(userPrincipal.getId());
            
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            platformPermissionService.revokePermission(userId, platform, revokedBy, ipAddress, userAgent);
            
            log.info("Permission revoked: {} access for user ID: {} by admin: {}", 
                platform, userId, revokedBy.getUsername());
            
            return ResponseEntity.ok(
                ApiResponse.success("Permission revoked successfully")
            );
            
        } catch (Exception ex) {
            log.error("Failed to revoke permission for user ID: {} on platform: {}", userId, platform, ex);
            return ResponseEntity.status(404).body(
                ApiResponse.error("Failed to revoke permission: " + ex.getMessage())
            );
        }
    }

    @GetMapping("/platforms")
    @Operation(summary = "Get available platforms", description = "Get list of all available platforms")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Platforms retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<String>>> getAvailablePlatforms() {
        
        try {
            List<String> platforms = List.of(
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
            );
            
            return ResponseEntity.ok(
                ApiResponse.success("Platforms retrieved successfully", platforms)
            );
            
        } catch (Exception ex) {
            log.error("Failed to retrieve available platforms", ex);
            return ResponseEntity.status(500).body(
                ApiResponse.error("Failed to retrieve platforms: " + ex.getMessage())
            );
        }
    }

    @GetMapping("/permission-levels")
    @Operation(summary = "Get permission levels", description = "Get list of all permission levels")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Permission levels retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<PermissionLevelInfo>>> getPermissionLevels() {
        
        try {
            List<PermissionLevelInfo> permissionLevels = List.of(
                new PermissionLevelInfo("READ_ONLY", "Read Only", "View configurations only"),
                new PermissionLevelInfo("READ_WRITE", "Read Write", "View and modify configurations"),
                new PermissionLevelInfo("ADMIN", "Admin", "Full administrative access to platform")
            );
            
            return ResponseEntity.ok(
                ApiResponse.success("Permission levels retrieved successfully", permissionLevels)
            );
            
        } catch (Exception ex) {
            log.error("Failed to retrieve permission levels", ex);
            return ResponseEntity.status(500).body(
                ApiResponse.error("Failed to retrieve permission levels: " + ex.getMessage())
            );
        }
    }

    private PlatformPermissionResponse convertToResponse(UserPlatformPermission permission) {
        return new PlatformPermissionResponse(
            permission.getId(),
            permission.getPlatform(),
            permission.getPermissionLevel().name(),
            permission.getPermissionLevel().getDisplayName(),
            permission.getEnabled(),
            permission.getCreatedAt(),
            permission.getUpdatedAt(),
            permission.getGrantedBy() != null ? permission.getGrantedBy().getUsername() : null
        );
    }

    private PlatformUserResponse convertToPlatformUserResponse(UserPlatformPermission permission) {
        User user = permission.getUser();
        return new PlatformUserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getEnabled(),
            permission.getPermissionLevel().name(),
            permission.getPermissionLevel().getDisplayName(),
            permission.getEnabled(),
            permission.getCreatedAt(),
            permission.getGrantedBy() != null ? permission.getGrantedBy().getUsername() : null
        );
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    // DTOs for request/response
    public static class GrantPermissionRequest {
        private String permissionLevel;
        
        public String getPermissionLevel() { return permissionLevel; }
        public void setPermissionLevel(String permissionLevel) { this.permissionLevel = permissionLevel; }
    }

    public static class UpdatePermissionRequest {
        private String permissionLevel;
        private boolean enabled = true;
        
        public String getPermissionLevel() { return permissionLevel; }
        public void setPermissionLevel(String permissionLevel) { this.permissionLevel = permissionLevel; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    public static class PlatformPermissionResponse {
        private Long id;
        private String platform;
        private String permissionLevel;
        private String permissionLevelDisplay;
        private boolean enabled;
        private java.time.LocalDateTime createdAt;
        private java.time.LocalDateTime updatedAt;
        private String grantedBy;

        public PlatformPermissionResponse(Long id, String platform, String permissionLevel, 
                                        String permissionLevelDisplay, boolean enabled,
                                        java.time.LocalDateTime createdAt, java.time.LocalDateTime updatedAt,
                                        String grantedBy) {
            this.id = id;
            this.platform = platform;
            this.permissionLevel = permissionLevel;
            this.permissionLevelDisplay = permissionLevelDisplay;
            this.enabled = enabled;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.grantedBy = grantedBy;
        }

        // Getters
        public Long getId() { return id; }
        public String getPlatform() { return platform; }
        public String getPermissionLevel() { return permissionLevel; }
        public String getPermissionLevelDisplay() { return permissionLevelDisplay; }
        public boolean isEnabled() { return enabled; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public java.time.LocalDateTime getUpdatedAt() { return updatedAt; }
        public String getGrantedBy() { return grantedBy; }
    }

    public static class PlatformUserResponse {
        private Long userId;
        private String username;
        private String email;
        private String fullName;
        private boolean userEnabled;
        private String permissionLevel;
        private String permissionLevelDisplay;
        private boolean permissionEnabled;
        private java.time.LocalDateTime grantedAt;
        private String grantedBy;

        public PlatformUserResponse(Long userId, String username, String email, String fullName,
                                  boolean userEnabled, String permissionLevel, String permissionLevelDisplay,
                                  boolean permissionEnabled, java.time.LocalDateTime grantedAt, String grantedBy) {
            this.userId = userId;
            this.username = username;
            this.email = email;
            this.fullName = fullName;
            this.userEnabled = userEnabled;
            this.permissionLevel = permissionLevel;
            this.permissionLevelDisplay = permissionLevelDisplay;
            this.permissionEnabled = permissionEnabled;
            this.grantedAt = grantedAt;
            this.grantedBy = grantedBy;
        }

        // Getters
        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
        public boolean isUserEnabled() { return userEnabled; }
        public String getPermissionLevel() { return permissionLevel; }
        public String getPermissionLevelDisplay() { return permissionLevelDisplay; }
        public boolean isPermissionEnabled() { return permissionEnabled; }
        public java.time.LocalDateTime getGrantedAt() { return grantedAt; }
        public String getGrantedBy() { return grantedBy; }
    }

    public static class PermissionLevelInfo {
        private String value;
        private String displayName;
        private String description;

        public PermissionLevelInfo(String value, String displayName, String description) {
            this.value = value;
            this.displayName = displayName;
            this.description = description;
        }

        // Getters
        public String getValue() { return value; }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
}

