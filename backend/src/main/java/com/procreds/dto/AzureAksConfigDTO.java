package com.procreds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Azure AKS configuration data transfer object")
public class AzureAksConfigDTO {

    @Schema(description = "Unique identifier", example = "507f1f77bcf86cd799439011", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Account name can only contain letters, numbers, underscores, and hyphens")
    @Schema(description = "Account name", example = "aks-prod", required = true)
    private String accountName;

    @NotBlank(message = "Cluster name is required")
    @Size(min = 2, max = 100, message = "Cluster name must be between 2 and 100 characters")
    @Schema(description = "AKS cluster name", example = "production-aks-cluster", required = true)
    private String clusterName;

    @NotBlank(message = "Tenant ID is required")
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 
             message = "Tenant ID must be a valid UUID format")
    @Schema(description = "Azure tenant ID", example = "12345678-1234-1234-1234-123456789012", required = true)
    private String tenantId;

    @NotBlank(message = "Client ID is required")
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 
             message = "Client ID must be a valid UUID format")
    @Schema(description = "Azure client ID (application ID)", example = "87654321-4321-4321-4321-210987654321", required = true)
    private String clientId;

    @NotBlank(message = "Client secret is required")
    @Size(min = 32, message = "Client secret must be at least 32 characters")
    @Schema(description = "Azure client secret", example = "8Q~xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", required = true)
    private String clientSecret;

    @Size(max = 255, message = "Description must be less than 255 characters")
    @Schema(description = "Configuration description", example = "Production AKS cluster in East US")
    private String description;

    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}

