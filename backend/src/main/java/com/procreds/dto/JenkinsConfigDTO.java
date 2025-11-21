package com.procreds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Jenkins configuration data transfer object")
public class JenkinsConfigDTO {

    @Schema(description = "Unique identifier", example = "507f1f77bcf86cd799439011", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Account name can only contain letters, numbers, underscores, and hyphens")
    @Schema(description = "Account name", example = "jenkins-prod", required = true)
    private String accountName;

    @NotBlank(message = "Base URL is required")
    @Pattern(regexp = "^https?://.+", message = "Base URL must be a valid HTTP or HTTPS URL")
    @Schema(description = "Jenkins server base URL", example = "https://jenkins.company.com", required = true)
    private String baseUrl;

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    @Schema(description = "Jenkins username", example = "admin", required = true)
    private String username;

    @NotBlank(message = "API token is required")
    @Size(min = 10, message = "API token must be at least 10 characters")
    @Schema(description = "Jenkins API token", example = "11xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", required = true)
    private String apiToken;

    @Size(max = 50, message = "Job prefix must be less than 50 characters")
    @Schema(description = "Job prefix for organization", example = "prod-")
    private String jobPrefix;

    @Size(max = 255, message = "Description must be less than 255 characters")
    @Schema(description = "Configuration description", example = "Production Jenkins server for CI/CD")
    private String description;

    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}

