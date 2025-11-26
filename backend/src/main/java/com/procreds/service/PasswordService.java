package com.procreds.service;

import com.procreds.dto.PasswordChangeRequest;
import com.procreds.entity.User;
import com.procreds.exception.BadRequestException;
import com.procreds.exception.ResourceNotFoundException;
import com.procreds.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final EmailService emailService;
    
    private static final int RESET_TOKEN_EXPIRY_HOURS = 24;
    private static final SecureRandom secureRandom = new SecureRandom();

    public void changePassword(String username, PasswordChangeRequest request, String ipAddress, String userAgent) {
        log.info("Changing password for user: {}", username);
        
        // Validate request
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }
        
        // Find user
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            auditService.logPasswordChange(user, false, "Invalid current password", ipAddress, userAgent);
            throw new BadRequestException("Current password is incorrect");
        }
        
        // Check if new password is different from current
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from current password");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setCredentialsNonExpired(true);
        userRepository.save(user);
        
        // Log successful password change
        auditService.logPasswordChange(user, true, "Password changed successfully", ipAddress, userAgent);
        
        log.info("Successfully changed password for user: {}", username);
    }

    public void initiatePasswordReset(String email, String ipAddress, String userAgent) {
        log.info("Initiating password reset for email: {}", email);
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        // Generate reset token
        String resetToken = generateSecureToken();
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(RESET_TOKEN_EXPIRY_HOURS);
        
        // Save reset token
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(expiryTime);
        userRepository.save(user);
        
        // Send reset email
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), resetToken);
            auditService.logPasswordReset(user, true, "Password reset email sent", ipAddress, userAgent);
            log.info("Password reset email sent to: {}", email);
        } catch (Exception ex) {
            log.error("Failed to send password reset email to: {}", email, ex);
            auditService.logPasswordReset(user, false, "Failed to send reset email: " + ex.getMessage(), ipAddress, userAgent);
            throw new BadRequestException("Failed to send password reset email");
        }
    }

    public boolean validateResetToken(String token) {
        log.info("Validating password reset token");
        
        User user = userRepository.findByPasswordResetToken(token).orElse(null);
        
        if (user == null) {
            log.warn("Invalid password reset token provided");
            return false;
        }
        
        if (user.getPasswordResetTokenExpiry() == null || 
            user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            log.warn("Expired password reset token for user: {}", user.getUsername());
            return false;
        }
        
        return true;
    }

    public void resetPassword(String token, String newPassword, String confirmPassword, 
                            String ipAddress, String userAgent) {
        log.info("Resetting password with token");
        
        // Validate passwords match
        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("New password and confirm password do not match");
        }
        
        // Find user by token
        User user = userRepository.findByPasswordResetToken(token)
            .orElseThrow(() -> new BadRequestException("Invalid or expired reset token"));
        
        // Check token expiry
        if (user.getPasswordResetTokenExpiry() == null || 
            user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            auditService.logPasswordReset(user, false, "Expired reset token used", ipAddress, userAgent);
            throw new BadRequestException("Reset token has expired");
        }
        
        // Check if new password is different from current
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BadRequestException("New password must be different from current password");
        }
        
        // Update password and clear reset token
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        user.setCredentialsNonExpired(true);
        user.setFailedLoginAttempts(0); // Reset failed attempts
        user.setAccountLockedUntil(null); // Unlock account if locked
        
        userRepository.save(user);
        
        // Log successful password reset
        auditService.logPasswordReset(user, true, "Password reset completed", ipAddress, userAgent);
        
        log.info("Successfully reset password for user: {}", user.getUsername());
    }

    public void adminResetPassword(Long userId, String newPassword, User adminUser, 
                                 String ipAddress, String userAgent) {
        log.info("Admin resetting password for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setCredentialsNonExpired(true);
        user.setFailedLoginAttempts(0); // Reset failed attempts
        user.setAccountLockedUntil(null); // Unlock account if locked
        user.setPasswordResetToken(null); // Clear any existing reset token
        user.setPasswordResetTokenExpiry(null);
        
        userRepository.save(user);
        
        // Log admin password reset
        auditService.logAction(adminUser, "ADMIN_PASSWORD_RESET", "user", user.getId().toString(), null,
            "Admin reset password for user: " + user.getUsername(), ipAddress, userAgent, true);
        
        log.info("Admin successfully reset password for user: {}", user.getUsername());
    }

    @Transactional(readOnly = true)
    public boolean isPasswordResetTokenValid(String token) {
        return validateResetToken(token);
    }

    public void cleanupExpiredResetTokens() {
        log.info("Cleaning up expired password reset tokens");
        
        List<User> usersWithExpiredTokens = userRepository.findUsersWithExpiredResetTokens(LocalDateTime.now());
        
        for (User user : usersWithExpiredTokens) {
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpiry(null);
            userRepository.save(user);
        }
        
        log.info("Cleaned up {} expired password reset tokens", usersWithExpiredTokens.size());
    }

    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
