package com.procreds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "GitLab configuration data transfer object")
public class GitLabConfigDTO {

    @Schema(description = "Unique identifier", example = "507f1f77bcf86cd799439011", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Account name can only contain letters, numbers, underscores, and hyphens")
    @Schema(description = "Account name", example = "john-doe", required = true)
    private String accountName;

    @NotBlank(message = "Personal access token is required")
    @Size(min = 10, message = "Personal access token must be at least 10 characters")
    @Schema(description = "GitLab personal access token", example = "glpat-xxxxxxxxxxxxxxxxxxxx", required = true)
    private String personalAccessToken;

    @Pattern(regexp = "^[0-9]+$", message = "Group ID must be numeric")
    @Schema(description = "GitLab group ID", example = "12345")
    private String groupId;

    @Pattern(regexp = "^https?://.+", message = "API URL must be a valid HTTP or HTTPS URL")
    @Schema(description = "Custom GitLab API URL", example = "https://gitlab.com/api/v4")
    private String apiUrl;

    @Size(max = 255, message = "Description must be less than 255 characters")
    @Schema(description = "Configuration description", example = "Production GitLab credentials for CI/CD")
    private String description;

    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}

