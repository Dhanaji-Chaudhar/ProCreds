package com.procreds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Bitbucket configuration data transfer object")
public class BitbucketConfigDTO {

    @Schema(description = "Unique identifier", example = "507f1f77bcf86cd799439011", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Username can only contain letters, numbers, underscores, and hyphens")
    @Schema(description = "Bitbucket username", example = "john_doe", required = true)
    private String username;

    @NotBlank(message = "App password is required")
    @Size(min = 10, message = "App password must be at least 10 characters")
    @Schema(description = "Bitbucket app password", example = "ATBBxxxxxxxxxxxxxxxxxx", required = true)
    private String appPassword;

    @Size(max = 50, message = "Workspace name must be less than 50 characters")
    @Schema(description = "Bitbucket workspace", example = "my-workspace")
    private String workspace;

    @Pattern(regexp = "^https?://.+", message = "API URL must be a valid HTTP or HTTPS URL")
    @Schema(description = "Custom Bitbucket API URL", example = "https://api.bitbucket.org/2.0")
    private String apiUrl;

    @Size(max = 255, message = "Description must be less than 255 characters")
    @Schema(description = "Configuration description", example = "Production Bitbucket credentials for CI/CD")
    private String description;

    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}

