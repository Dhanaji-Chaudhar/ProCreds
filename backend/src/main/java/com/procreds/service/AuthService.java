package com.procreds.service;

import com.procreds.dto.LoginRequest;
import com.procreds.dto.LoginResponse;
import com.procreds.dto.UserProfileResponse;
import com.procreds.entity.Role;
import com.procreds.entity.User;
import com.procreds.entity.UserPlatformPermission;
import com.procreds.exception.BadRequestException;
import com.procreds.exception.UnauthorizedException;
import com.procreds.repository.UserPlatformPermissionRepository;
import com.procreds.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserPlatformPermissionRepository permissionRepository;
    private final AuditService auditService;

    @Value("${app.jwt.expiration:86400000}") // 24 hours in milliseconds
    private int jwtExpirationMs;

    public LoginResponse authenticateUser(LoginRequest loginRequest, String ipAddress, String userAgent) {
        log.info("Attempting to authenticate user: {}", loginRequest.getUsername());
        
        try {
            // Check if user exists and is not locked
            User user = userService.findByUsername(loginRequest.getUsername());
            
            if (user.isAccountLocked()) {
                log.warn("Login attempt for locked account: {}", loginRequest.getUsername());
                auditService.logLogin(user, false, "Account is locked", ipAddress, userAgent);
                throw new LockedException("Account is locked due to multiple failed login attempts");
            }
            
            if (!user.getEnabled()) {
                log.warn("Login attempt for disabled account: {}", loginRequest.getUsername());
                auditService.logLogin(user, false, "Account is disabled", ipAddress, userAgent);
                throw new DisabledException("Account is disabled");
            }
            
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            // Generate tokens
            String accessToken = jwtUtil.generateToken(authentication);
            String refreshToken = jwtUtil.generateRefreshToken(loginRequest.getUsername());
            
            // Update last login time and reset failed attempts
            userService.updateLastLogin(loginRequest.getUsername());
            
            // Get user profile and permissions
            UserProfileResponse userProfile = userService.getUserProfile(user.getId());
            List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
            List<String> platforms = userService.getUserAccessiblePlatforms(user.getId());
            
            // Create login response
            LoginResponse response = new LoginResponse(
                accessToken,
                refreshToken,
                (long) jwtExpirationMs,
                userProfile,
                roles,
                platforms
            );
            
            // Log successful login
            auditService.logLogin(user, true, "Login successful", ipAddress, userAgent);
            
            log.info("Successfully authenticated user: {}", loginRequest.getUsername());
            return response;
            
        } catch (BadCredentialsException ex) {
            log.warn("Invalid credentials for user: {}", loginRequest.getUsername());
            
            // Increment failed login attempts
            try {
                userService.incrementFailedLoginAttempts(loginRequest.getUsername());
                User user = userService.findByUsername(loginRequest.getUsername());
                auditService.logLogin(user, false, "Invalid credentials", ipAddress, userAgent);
            } catch (Exception e) {
                log.error("Error updating failed login attempts for user: {}", loginRequest.getUsername(), e);
            }
            
            throw new UnauthorizedException("Invalid username or password");
            
        } catch (DisabledException ex) {
            throw new UnauthorizedException("Account is disabled");
            
        } catch (LockedException ex) {
            throw new UnauthorizedException("Account is locked due to multiple failed login attempts");
            
        } catch (AuthenticationException ex) {
            log.error("Authentication failed for user: {}", loginRequest.getUsername(), ex);
            throw new UnauthorizedException("Authentication failed");
        }
    }

    public LoginResponse refreshToken(String refreshToken, String ipAddress, String userAgent) {
        log.info("Attempting to refresh token");
        
        try {
            // Validate refresh token
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new UnauthorizedException("Invalid refresh token");
            }
            
            // Extract username from token
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            User user = userService.findByUsername(username);
            
            // Check if user is still active
            if (!user.getEnabled()) {
                throw new UnauthorizedException("Account is disabled");
            }
            
            if (user.isAccountLocked()) {
                throw new UnauthorizedException("Account is locked");
            }
            
            // Generate new tokens
            String newAccessToken = jwtUtil.generateTokenFromUsername(username);
            String newRefreshToken = jwtUtil.generateRefreshToken(username);
            
            // Get user profile and permissions
            UserProfileResponse userProfile = userService.getUserProfile(user.getId());
            List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
            List<String> platforms = userService.getUserAccessiblePlatforms(user.getId());
            
            // Create response
            LoginResponse response = new LoginResponse(
                newAccessToken,
                newRefreshToken,
                (long) jwtExpirationMs,
                userProfile,
                roles,
                platforms
            );
            
            // Log token refresh
            auditService.logAction(user, "TOKEN_REFRESH", "auth", null, null, 
                "Token refreshed successfully", ipAddress, userAgent, true);
            
            log.info("Successfully refreshed token for user: {}", username);
            return response;
            
        } catch (Exception ex) {
            log.error("Token refresh failed", ex);
            throw new UnauthorizedException("Token refresh failed");
        }
    }

    public void logout(String username, String ipAddress, String userAgent) {
        log.info("User logout: {}", username);
        
        try {
            User user = userService.findByUsername(username);
            auditService.logLogout(user, ipAddress, userAgent);
            
            // Note: In a stateless JWT system, logout is typically handled client-side
            // by removing the token. For enhanced security, you could implement a 
            // token blacklist or use shorter token expiration times.
            
            log.info("Successfully logged out user: {}", username);
            
        } catch (Exception ex) {
            log.error("Error during logout for user: {}", username, ex);
        }
    }

    @Transactional(readOnly = true)
    public boolean hasUserPlatformPermission(String username, String platform, UserPlatformPermission.PermissionLevel requiredLevel) {
        try {
            User user = userService.findByUsername(username);
            
            // Admin users have access to all platforms
            if (user.hasRole(Role.ADMIN)) {
                return true;
            }
            
            // Check platform-specific permission
            UserPlatformPermission.PermissionLevel userLevel = userService.getUserPlatformPermissionLevel(user.getId(), platform);
            
            if (userLevel == null) {
                return false;
            }
            
            // Check if user's permission level meets the required level
            return hasRequiredPermissionLevel(userLevel, requiredLevel);
            
        } catch (Exception ex) {
            log.error("Error checking platform permission for user: {} on platform: {}", username, platform, ex);
            return false;
        }
    }

    @Transactional(readOnly = true)
    public boolean canUserAccessPlatform(String username, String platform) {
        return hasUserPlatformPermission(username, platform, UserPlatformPermission.PermissionLevel.READ_ONLY);
    }

    @Transactional(readOnly = true)
    public boolean canUserModifyPlatform(String username, String platform) {
        return hasUserPlatformPermission(username, platform, UserPlatformPermission.PermissionLevel.READ_WRITE);
    }

    @Transactional(readOnly = true)
    public boolean canUserAdministerPlatform(String username, String platform) {
        return hasUserPlatformPermission(username, platform, UserPlatformPermission.PermissionLevel.ADMIN);
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

    @Transactional(readOnly = true)
    public void validateTokenAndUser(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new UnauthorizedException("Invalid or expired token");
        }
        
        String username = jwtUtil.getUsernameFromToken(token);
        User user = userService.findByUsername(username);
        
        if (!user.getEnabled()) {
            throw new UnauthorizedException("Account is disabled");
        }
        
        if (user.isAccountLocked()) {
            throw new UnauthorizedException("Account is locked");
        }
    }
}

