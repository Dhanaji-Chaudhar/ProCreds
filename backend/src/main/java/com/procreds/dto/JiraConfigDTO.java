package com.procreds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Jira configuration data transfer object")
public class JiraConfigDTO {

    @Schema(description = "Unique identifier", example = "507f1f77bcf86cd799439011", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Account name can only contain letters, numbers, underscores, and hyphens")
    @Schema(description = "Account name", example = "jira-prod", required = true)
    private String accountName;

    @NotBlank(message = "Base URL is required")
    @Pattern(regexp = "^https?://.+", message = "Base URL must be a valid HTTP or HTTPS URL")
    @Schema(description = "Jira server base URL", example = "https://company.atlassian.net", required = true)
    private String baseUrl;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "Jira user email", example = "admin@company.com", required = true)
    private String email;

    @NotBlank(message = "API token is required")
    @Size(min = 10, message = "API token must be at least 10 characters")
    @Schema(description = "Jira API token", example = "ATATTxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", required = true)
    private String apiToken;

    @Size(max = 20, message = "Project key must be less than 20 characters")
    @Pattern(regexp = "^[A-Z][A-Z0-9]*$", message = "Project key must start with a letter and contain only uppercase letters and numbers")
    @Schema(description = "Default project key", example = "PROJ")
    private String projectKey;

    @Size(max = 50, message = "Issue type must be less than 50 characters")
    @Schema(description = "Default issue type", example = "Task")
    private String issueTypeDefault;

    @Size(max = 255, message = "Description must be less than 255 characters")
    @Schema(description = "Configuration description", example = "Production Jira instance for project management")
    private String description;

    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}

