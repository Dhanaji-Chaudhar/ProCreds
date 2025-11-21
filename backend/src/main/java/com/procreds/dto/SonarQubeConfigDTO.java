package com.procreds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "SonarQube configuration data transfer object")
public class SonarQubeConfigDTO {

    @Schema(description = "Unique identifier", example = "507f1f77bcf86cd799439011", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Account name can only contain letters, numbers, underscores, and hyphens")
    @Schema(description = "Account name", example = "sonar-prod", required = true)
    private String accountName;

    @NotBlank(message = "Base URL is required")
    @Pattern(regexp = "^https?://.+", message = "Base URL must be a valid HTTP or HTTPS URL")
    @Schema(description = "SonarQube server base URL", example = "https://sonarqube.company.com", required = true)
    private String baseUrl;

    @NotBlank(message = "Token is required")
    @Size(min = 10, message = "Token must be at least 10 characters")
    @Schema(description = "SonarQube authentication token", example = "squ_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", required = true)
    private String token;

    @Size(max = 50, message = "Organization must be less than 50 characters")
    @Schema(description = "SonarQube organization", example = "my-organization")
    private String organization;

    @Size(max = 255, message = "Description must be less than 255 characters")
    @Schema(description = "Configuration description", example = "Production SonarQube server for code quality")
    private String description;

    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}

