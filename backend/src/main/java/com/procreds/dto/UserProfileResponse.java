package com.procreds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private boolean enabled;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private List<String> roles;
    private List<PlatformPermissionResponse> platformPermissions;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlatformPermissionResponse {
        private String platform;
        private String permissionLevel;
        private boolean enabled;
        private LocalDateTime grantedAt;
        private String grantedBy;
    }
}

