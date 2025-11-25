package com.procreds.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_platform_permissions", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "platform"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserPlatformPermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotBlank
    @Column(nullable = false)
    private String platform;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "permission_level", nullable = false)
    private PermissionLevel permissionLevel = PermissionLevel.READ_WRITE;
    
    @Column(nullable = false)
    private boolean enabled = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by_user_id")
    private User grantedBy;
    
    public enum PermissionLevel {
        READ_ONLY("Read Only"),
        READ_WRITE("Read Write"),
        ADMIN("Admin");
        
        private final String displayName;
        
        PermissionLevel(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Platform constants
    public static final String GITHUB = "github";
    public static final String BITBUCKET = "bitbucket";
    public static final String GITLAB = "gitlab";
    public static final String JENKINS = "jenkins";
    public static final String JIRA = "jira";
    public static final String SONARQUBE = "sonarqube";
    public static final String KUBERNETES = "kubernetes";
    public static final String AWS_EKS = "aws-eks";
    public static final String AZURE_AKS = "azure-aks";
    public static final String GCP_GKE = "gcp-gke";
    
    public UserPlatformPermission(User user, String platform, PermissionLevel permissionLevel, User grantedBy) {
        this.user = user;
        this.platform = platform;
        this.permissionLevel = permissionLevel;
        this.grantedBy = grantedBy;
    }
}

