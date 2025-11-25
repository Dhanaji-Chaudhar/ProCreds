package com.procreds.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserProfileResponse user;
    private List<String> roles;
    private List<String> platforms;
    private LocalDateTime loginTime;
    
    public LoginResponse(String accessToken, String refreshToken, Long expiresIn, 
                        UserProfileResponse user, List<String> roles, List<String> platforms) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.user = user;
        this.roles = roles;
        this.platforms = platforms;
        this.loginTime = LocalDateTime.now();
    }
}

