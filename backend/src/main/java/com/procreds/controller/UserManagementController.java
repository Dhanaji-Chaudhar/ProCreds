package com.procreds.controller;

import com.procreds.dto.ApiResponse;
import com.procreds.dto.UserProfileResponse;
import com.procreds.dto.UserRegistrationRequest;
import com.procreds.entity.User;
import com.procreds.service.CustomUserDetailsService;
import com.procreds.service.PasswordService;
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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "User management endpoints for administrators")
public class UserManagementController {

    private final UserService userService;
    private final PasswordService passwordService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Get paginated list of all users (Admin only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Page<UserProfileResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean enabled) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<User> users;
            if (search != null && !search.trim().isEmpty()) {
                users = userService.searchUsers(search.trim(), pageable);
            } else if (enabled != null) {
                users = userService.findUsersByEnabled(enabled, pageable);
            } else {
                users = userService.findAllUsers(pageable);
            }
            
            Page<UserProfileResponse> userProfiles = users.map(user -> 
                userService.getUserProfile(user.getId()));
            
            return ResponseEntity.ok(
                ApiResponse.success("Users retrieved successfully", userProfiles)
            );
            
        } catch (Exception ex) {
            log.error("Failed to retrieve users", ex);
            return ResponseEntity.status(500).body(
                ApiResponse.error("Failed to retrieve users: " + ex.getMessage())
            );
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Get user by ID", description = "Get user details by ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(@PathVariable Long id) {
        
        try {
            UserProfileResponse userProfile = userService.getUserProfile(id);
            
            return ResponseEntity.ok(
                ApiResponse.success("User retrieved successfully", userProfile)
            );
            
        } catch (Exception ex) {
            log.error("Failed to retrieve user with ID: {}", id, ex);
            return ResponseEntity.status(404).body(
                ApiResponse.error("User not found: " + ex.getMessage())
            );
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new user", description = "Create a new user (Admin only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<UserProfileResponse>> createUser(
            @Valid @RequestBody UserRegistrationRequest userRegistrationRequest,
            HttpServletRequest request) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetailsService.UserPrincipal userPrincipal = 
                (CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            User createdBy = userService.findById(userPrincipal.getId());
            
            User newUser = userService.createUser(userRegistrationRequest, createdBy);
            UserProfileResponse userProfile = userService.getUserProfile(newUser.getId());
            
            log.info("User created successfully: {} by admin: {}", newUser.getUsername(), createdBy.getUsername());
            
            return ResponseEntity.status(201).body(
                ApiResponse.success("User created successfully", userProfile)
            );
            
        } catch (Exception ex) {
            log.error("Failed to create user", ex);
            return ResponseEntity.status(400).body(
                ApiResponse.error("Failed to create user: " + ex.getMessage())
            );
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Update user details (Admin only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRegistrationRequest userRegistrationRequest,
            HttpServletRequest request) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetailsService.UserPrincipal userPrincipal = 
                (CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            User updatedBy = userService.findById(userPrincipal.getId());
            
            User updatedUser = userService.updateUser(id, userRegistrationRequest, updatedBy);
            UserProfileResponse userProfile = userService.getUserProfile(updatedUser.getId());
            
            log.info("User updated successfully: {} by admin: {}", updatedUser.getUsername(), updatedBy.getUsername());
            
            return ResponseEntity.ok(
                ApiResponse.success("User updated successfully", userProfile)
            );
            
        } catch (Exception ex) {
            log.error("Failed to update user with ID: {}", id, ex);
            return ResponseEntity.status(400).body(
                ApiResponse.error("Failed to update user: " + ex.getMessage())
            );
        }
    }

    @PutMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enable user", description = "Enable user account (Admin only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User enabled successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> enableUser(@PathVariable Long id) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetailsService.UserPrincipal userPrincipal = 
                (CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            User enabledBy = userService.findById(userPrincipal.getId());
            
            userService.enableUser(id, enabledBy);
            
            log.info("User enabled successfully: ID {} by admin: {}", id, enabledBy.getUsername());
            
            return ResponseEntity.ok(
                ApiResponse.success("User enabled successfully")
            );
            
        } catch (Exception ex) {
            log.error("Failed to enable user with ID: {}", id, ex);
            return ResponseEntity.status(404).body(
                ApiResponse.error("Failed to enable user: " + ex.getMessage())
            );
        }
    }

    @PutMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Disable user", description = "Disable user account (Admin only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User disabled successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> disableUser(@PathVariable Long id) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetailsService.UserPrincipal userPrincipal = 
                (CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            User disabledBy = userService.findById(userPrincipal.getId());
            
            userService.disableUser(id, disabledBy);
            
            log.info("User disabled successfully: ID {} by admin: {}", id, disabledBy.getUsername());
            
            return ResponseEntity.ok(
                ApiResponse.success("User disabled successfully")
            );
            
        } catch (Exception ex) {
            log.error("Failed to disable user with ID: {}", id, ex);
            return ResponseEntity.status(404).body(
                ApiResponse.error("Failed to disable user: " + ex.getMessage())
            );
        }
    }

    @PutMapping("/{id}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unlock user", description = "Unlock user account (Admin only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User unlocked successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> unlockUser(@PathVariable Long id) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetailsService.UserPrincipal userPrincipal = 
                (CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            User unlockedBy = userService.findById(userPrincipal.getId());
            
            userService.unlockUser(id, unlockedBy);
            
            log.info("User unlocked successfully: ID {} by admin: {}", id, unlockedBy.getUsername());
            
            return ResponseEntity.ok(
                ApiResponse.success("User unlocked successfully")
            );
            
        } catch (Exception ex) {
            log.error("Failed to unlock user with ID: {}", id, ex);
            return ResponseEntity.status(404).body(
                ApiResponse.error("Failed to unlock user: " + ex.getMessage())
            );
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Delete user account (Admin only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetailsService.UserPrincipal userPrincipal = 
                (CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            User deletedBy = userService.findById(userPrincipal.getId());
            
            // Prevent self-deletion
            if (id.equals(userPrincipal.getId())) {
                return ResponseEntity.status(400).body(
                    ApiResponse.error("Cannot delete your own account")
                );
            }
            
            userService.deleteUser(id, deletedBy);
            
            log.info("User deleted successfully: ID {} by admin: {}", id, deletedBy.getUsername());
            
            return ResponseEntity.ok(
                ApiResponse.success("User deleted successfully")
            );
            
        } catch (Exception ex) {
            log.error("Failed to delete user with ID: {}", id, ex);
            return ResponseEntity.status(404).body(
                ApiResponse.error("Failed to delete user: " + ex.getMessage())
            );
        }
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin reset password", description = "Reset user password (Admin only)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ApiResponse<Void>> adminResetPassword(
            @PathVariable Long id,
            @RequestBody AdminPasswordResetRequest adminPasswordResetRequest,
            HttpServletRequest request) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetailsService.UserPrincipal userPrincipal = 
                (CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            User adminUser = userService.findById(userPrincipal.getId());
            
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            passwordService.adminResetPassword(
                id, 
                adminPasswordResetRequest.getNewPassword(), 
                adminUser, 
                ipAddress, 
                userAgent
            );
            
            log.info("Password reset by admin: {} for user ID: {}", adminUser.getUsername(), id);
            
            return ResponseEntity.ok(
                ApiResponse.success("Password reset successfully")
            );
            
        } catch (Exception ex) {
            log.error("Failed to reset password for user ID: {}", id, ex);
            return ResponseEntity.status(404).body(
                ApiResponse.error("Failed to reset password: " + ex.getMessage())
            );
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile", description = "Get current authenticated user's profile")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUserProfile() {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetailsService.UserPrincipal userPrincipal = 
                (CustomUserDetailsService.UserPrincipal) authentication.getPrincipal();
            
            UserProfileResponse userProfile = userService.getUserProfile(userPrincipal.getId());
            
            return ResponseEntity.ok(
                ApiResponse.success("Profile retrieved successfully", userProfile)
            );
            
        } catch (Exception ex) {
            log.error("Failed to get current user profile", ex);
            return ResponseEntity.status(500).body(
                ApiResponse.error("Failed to get profile: " + ex.getMessage())
            );
        }
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

    // Additional DTO for admin password reset
    public static class AdminPasswordResetRequest {
        private String newPassword;
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}

