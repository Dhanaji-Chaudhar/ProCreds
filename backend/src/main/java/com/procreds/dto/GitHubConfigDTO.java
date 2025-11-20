package com.procreds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for GitHub configuration
 * 
 * Used for API requests and responses, providing validation and documentation.
 * Separates external API contract from internal entity structure.
 * 
 * @author ProCreds Team
 */
@Data
@Schema(description = "GitHub platform configuration")
public class GitHubConfigDTO {

    @Schema(description = "Unique identifier", example = "507f1f77bcf86cd799439011", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @Schema(description = "GitHub account name (unique)", example = "john-doe", required = true)
    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Account name must contain only alphanumeric characters and hyphens")
    private String accountName;

    @Schema(description = "GitHub personal access token", example = "ghp_xxxxxxxxxxxxxxxxxxxx", required = true)
    @NotBlank(message = "Access token is required")
    @Size(min = 10, message = "Access token must be at least 10 characters")
    private String accessToken;

    @Schema(description = "GitHub organization name (optional)", example = "my-organization")
    @Size(max = 50, message = "Organization name cannot exceed 50 characters")
    private String organization;

    @Schema(description = "Custom GitHub API URL (optional)", example = "https://api.github.com")
    @Pattern(regexp = "^https?://.+", message = "API URL must be a valid HTTP/HTTPS URL")
    private String apiUrl;

    @Schema(description = "Configuration description", example = "Production GitHub credentials for CI/CD")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}

