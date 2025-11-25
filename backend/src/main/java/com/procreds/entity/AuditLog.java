package com.procreds.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @NotBlank
    @Column(nullable = false)
    private String action;
    
    @Column(name = "resource_type")
    private String resourceType;
    
    @Column(name = "resource_id")
    private String resourceId;
    
    @Column(name = "platform")
    private String platform;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "user_agent")
    private String userAgent;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionResult result = ActionResult.SUCCESS;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum ActionResult {
        SUCCESS,
        FAILURE,
        UNAUTHORIZED,
        FORBIDDEN
    }
    
    // Action constants
    public static final String LOGIN = "LOGIN";
    public static final String LOGOUT = "LOGOUT";
    public static final String PASSWORD_CHANGE = "PASSWORD_CHANGE";
    public static final String PASSWORD_RESET = "PASSWORD_RESET";
    public static final String USER_CREATE = "USER_CREATE";
    public static final String USER_UPDATE = "USER_UPDATE";
    public static final String USER_DELETE = "USER_DELETE";
    public static final String USER_ENABLE = "USER_ENABLE";
    public static final String USER_DISABLE = "USER_DISABLE";
    public static final String PERMISSION_GRANT = "PERMISSION_GRANT";
    public static final String PERMISSION_REVOKE = "PERMISSION_REVOKE";
    public static final String CONFIG_CREATE = "CONFIG_CREATE";
    public static final String CONFIG_UPDATE = "CONFIG_UPDATE";
    public static final String CONFIG_DELETE = "CONFIG_DELETE";
    public static final String CONFIG_VIEW = "CONFIG_VIEW";
    public static final String TEST_CONNECTION = "TEST_CONNECTION";
    
    public AuditLog(User user, String action, String resourceType, String resourceId, 
                   String platform, String ipAddress, ActionResult result) {
        this.user = user;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.platform = platform;
        this.ipAddress = ipAddress;
        this.result = result;
    }
}

