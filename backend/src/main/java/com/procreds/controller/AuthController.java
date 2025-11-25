package com.procreds.controller;

import com.procreds.dto.*;
import com.procreds.service.AuthService;
import com.procreds.service.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and password management endpoints")
public class AuthController {

    private final AuthService authService;
    private final PasswordService passwordService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "423", description = "Account locked"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<com.procreds.dto.ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        
        try {
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            LoginResponse loginResponse = authService.authenticateUser(loginRequest, ipAddress, userAgent);
            
            return ResponseEntity.ok(
                com.procreds.dto.ApiResponse.success("Login successful", loginResponse)
            );
            
        } catch (Exception ex) {
            log.error("Login failed for user: {}", loginRequest.getUsername(), ex);
            return ResponseEntity.status(401).body(
                com.procreds.dto.ApiResponse.error("Authentication failed: " + ex.getMessage())
            );
        }
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh JWT token", description = "Generate new access token using refresh token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    public ResponseEntity<com.procreds.dto.ApiResponse<LoginResponse>> refreshToken(
            @RequestBody RefreshTokenRequest refreshTokenRequest,
            HttpServletRequest request) {
        
        log.info("Token refresh attempt");
        
        try {
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            LoginResponse loginResponse = authService.refreshToken(
                refreshTokenRequest.getRefreshToken(), ipAddress, userAgent);
            
            return ResponseEntity.ok(
                com.procreds.dto.ApiResponse.success("Token refreshed successfully", loginResponse)
            );
            
        } catch (Exception ex) {
            log.error("Token refresh failed", ex);
            return ResponseEntity.status(401).body(
                com.procreds.dto.ApiResponse.error("Token refresh failed: " + ex.getMessage())
            );
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user and invalidate session")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<com.procreds.dto.ApiResponse<Void>> logout(HttpServletRequest request) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            authService.logout(username, ipAddress, userAgent);
            
            log.info("User logged out: {}", username);
            
            return ResponseEntity.ok(
                com.procreds.dto.ApiResponse.success("Logout successful")
            );
            
        } catch (Exception ex) {
            log.error("Logout failed", ex);
            return ResponseEntity.status(500).body(
                com.procreds.dto.ApiResponse.error("Logout failed: " + ex.getMessage())
            );
        }
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change user's password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid current password or validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<com.procreds.dto.ApiResponse<Void>> changePassword(
            @Valid @RequestBody PasswordChangeRequest passwordChangeRequest,
            HttpServletRequest request) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            passwordService.changePassword(username, passwordChangeRequest, ipAddress, userAgent);
            
            log.info("Password changed successfully for user: {}", username);
            
            return ResponseEntity.ok(
                com.procreds.dto.ApiResponse.success("Password changed successfully")
            );
            
        } catch (Exception ex) {
            log.error("Password change failed", ex);
            return ResponseEntity.status(400).body(
                com.procreds.dto.ApiResponse.error("Password change failed: " + ex.getMessage())
            );
        }
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Initiate password reset process")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset email sent"),
        @ApiResponse(responseCode = "404", description = "Email not found"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<com.procreds.dto.ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody PasswordResetRequest passwordResetRequest,
            HttpServletRequest request) {
        
        log.info("Password reset requested for email: {}", passwordResetRequest.getEmail());
        
        try {
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            passwordService.initiatePasswordReset(passwordResetRequest.getEmail(), ipAddress, userAgent);
            
            return ResponseEntity.ok(
                com.procreds.dto.ApiResponse.success("If the email exists, a password reset link has been sent")
            );
            
        } catch (Exception ex) {
            log.error("Password reset initiation failed for email: {}", passwordResetRequest.getEmail(), ex);
            // Always return success to prevent email enumeration
            return ResponseEntity.ok(
                com.procreds.dto.ApiResponse.success("If the email exists, a password reset link has been sent")
            );
        }
    }

    @PostMapping("/validate-reset-token")
    @Operation(summary = "Validate reset token", description = "Validate password reset token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token is valid"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    public ResponseEntity<com.procreds.dto.ApiResponse<Void>> validateResetToken(
            @RequestBody TokenValidationRequest tokenValidationRequest) {
        
        log.info("Validating password reset token");
        
        try {
            boolean isValid = passwordService.validateResetToken(tokenValidationRequest.getToken());
            
            if (isValid) {
                return ResponseEntity.ok(
                    com.procreds.dto.ApiResponse.success("Token is valid")
                );
            } else {
                return ResponseEntity.status(400).body(
                    com.procreds.dto.ApiResponse.error("Invalid or expired token")
                );
            }
            
        } catch (Exception ex) {
            log.error("Token validation failed", ex);
            return ResponseEntity.status(400).body(
                com.procreds.dto.ApiResponse.error("Token validation failed")
            );
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using reset token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid token or validation error")
    })
    public ResponseEntity<com.procreds.dto.ApiResponse<Void>> resetPassword(
            @Valid @RequestBody PasswordResetConfirmRequest passwordResetConfirmRequest,
            HttpServletRequest request) {
        
        log.info("Password reset confirmation attempt");
        
        try {
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            passwordService.resetPassword(
                passwordResetConfirmRequest.getToken(),
                passwordResetConfirmRequest.getNewPassword(),
                passwordResetConfirmRequest.getConfirmPassword(),
                ipAddress,
                userAgent
            );
            
            return ResponseEntity.ok(
                com.procreds.dto.ApiResponse.success("Password reset successfully")
            );
            
        } catch (Exception ex) {
            log.error("Password reset failed", ex);
            return ResponseEntity.status(400).body(
                com.procreds.dto.ApiResponse.error("Password reset failed: " + ex.getMessage())
            );
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Get current user's profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<com.procreds.dto.ApiResponse<UserProfileResponse>> getUserProfile() {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            // This would require UserService to have a method to get profile by username
            // For now, we'll return a placeholder response
            
            return ResponseEntity.ok(
                com.procreds.dto.ApiResponse.success("Profile retrieved successfully", null)
            );
            
        } catch (Exception ex) {
            log.error("Failed to get user profile", ex);
            return ResponseEntity.status(500).body(
                com.procreds.dto.ApiResponse.error("Failed to get user profile")
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

    // Additional DTOs for request bodies
    public static class RefreshTokenRequest {
        private String refreshToken;
        
        public String getRefreshToken() { return refreshToken; }
        public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    }

    public static class TokenValidationRequest {
        private String token;
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public static class PasswordResetConfirmRequest {
        private String token;
        private String newPassword;
        private String confirmPassword;
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
        
        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    }
}

