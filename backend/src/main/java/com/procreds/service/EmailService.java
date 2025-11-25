package com.procreds.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@procreds.com}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String fullName, String resetToken) {
        log.info("Sending password reset email to: {}", toEmail);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("ProCreds - Password Reset Request");
            
            String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "You have requested to reset your password for your ProCreds account.\n\n" +
                "Please click the following link to reset your password:\n" +
                "%s\n\n" +
                "This link will expire in 24 hours for security reasons.\n\n" +
                "If you did not request this password reset, please ignore this email and your password will remain unchanged.\n\n" +
                "For security reasons, please do not share this link with anyone.\n\n" +
                "Best regards,\n" +
                "The ProCreds Team",
                fullName, resetUrl
            );
            
            message.setText(emailBody);
            
            mailSender.send(message);
            
            log.info("Password reset email sent successfully to: {}", toEmail);
            
        } catch (Exception ex) {
            log.error("Failed to send password reset email to: {}", toEmail, ex);
            throw new RuntimeException("Failed to send password reset email", ex);
        }
    }

    public void sendWelcomeEmail(String toEmail, String fullName, String username, String temporaryPassword) {
        log.info("Sending welcome email to: {}", toEmail);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to ProCreds - Your Account Has Been Created");
            
            String loginUrl = frontendUrl + "/login";
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "Welcome to ProCreds! Your account has been successfully created.\n\n" +
                "Your login credentials are:\n" +
                "Username: %s\n" +
                "Temporary Password: %s\n\n" +
                "Please log in at: %s\n\n" +
                "For security reasons, please change your password immediately after your first login.\n\n" +
                "If you have any questions or need assistance, please contact your system administrator.\n\n" +
                "Best regards,\n" +
                "The ProCreds Team",
                fullName, username, temporaryPassword, loginUrl
            );
            
            message.setText(emailBody);
            
            mailSender.send(message);
            
            log.info("Welcome email sent successfully to: {}", toEmail);
            
        } catch (Exception ex) {
            log.error("Failed to send welcome email to: {}", toEmail, ex);
            throw new RuntimeException("Failed to send welcome email", ex);
        }
    }

    public void sendAccountStatusEmail(String toEmail, String fullName, boolean enabled) {
        log.info("Sending account status email to: {}", toEmail);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            
            String status = enabled ? "Enabled" : "Disabled";
            message.setSubject("ProCreds - Account " + status);
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "Your ProCreds account has been %s.\n\n" +
                "%s\n\n" +
                "If you have any questions about this change, please contact your system administrator.\n\n" +
                "Best regards,\n" +
                "The ProCreds Team",
                fullName, 
                status.toLowerCase(),
                enabled ? "You can now log in and access your assigned platforms." : 
                         "You will no longer be able to access the system until your account is re-enabled."
            );
            
            message.setText(emailBody);
            
            mailSender.send(message);
            
            log.info("Account status email sent successfully to: {}", toEmail);
            
        } catch (Exception ex) {
            log.error("Failed to send account status email to: {}", toEmail, ex);
            // Don't throw exception for account status emails as they're not critical
            log.warn("Continuing without sending account status email");
        }
    }

    public void sendPlatformAccessEmail(String toEmail, String fullName, String platform, 
                                      String permissionLevel, boolean granted) {
        log.info("Sending platform access email to: {}", toEmail);
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            
            String action = granted ? "Granted" : "Revoked";
            message.setSubject("ProCreds - Platform Access " + action);
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "Your access to the %s platform has been %s.\n\n" +
                "%s\n\n" +
                "You can log in to ProCreds to view your current platform permissions.\n\n" +
                "If you have any questions about this change, please contact your system administrator.\n\n" +
                "Best regards,\n" +
                "The ProCreds Team",
                fullName, 
                platform,
                action.toLowerCase(),
                granted ? String.format("Permission Level: %s", permissionLevel) : 
                         "You no longer have access to this platform."
            );
            
            message.setText(emailBody);
            
            mailSender.send(message);
            
            log.info("Platform access email sent successfully to: {}", toEmail);
            
        } catch (Exception ex) {
            log.error("Failed to send platform access email to: {}", toEmail, ex);
            // Don't throw exception for platform access emails as they're not critical
            log.warn("Continuing without sending platform access email");
        }
    }
}

