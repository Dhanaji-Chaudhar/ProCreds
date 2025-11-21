package com.procreds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "AWS EKS configuration data transfer object")
public class AwsEksConfigDTO {

    @Schema(description = "Unique identifier", example = "507f1f77bcf86cd799439011", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Account name can only contain letters, numbers, underscores, and hyphens")
    @Schema(description = "Account name", example = "eks-prod", required = true)
    private String accountName;

    @NotBlank(message = "Cluster name is required")
    @Size(min = 2, max = 100, message = "Cluster name must be between 2 and 100 characters")
    @Schema(description = "EKS cluster name", example = "production-eks-cluster", required = true)
    private String clusterName;

    @NotBlank(message = "Access key is required")
    @Size(min = 16, max = 32, message = "Access key must be between 16 and 32 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Access key must contain only uppercase letters and numbers")
    @Schema(description = "AWS access key ID", example = "AKIAIOSFODNN7EXAMPLE", required = true)
    private String accessKey;

    @NotBlank(message = "Secret key is required")
    @Size(min = 32, message = "Secret key must be at least 32 characters")
    @Schema(description = "AWS secret access key", example = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY", required = true)
    private String secretKey;

    @NotBlank(message = "Region is required")
    @Pattern(regexp = "^[a-z]{2}-[a-z]+-[0-9]$", message = "Region must be a valid AWS region format (e.g., us-west-2)")
    @Schema(description = "AWS region", example = "us-west-2", required = true)
    private String region;

    @Size(max = 255, message = "Description must be less than 255 characters")
    @Schema(description = "Configuration description", example = "Production EKS cluster in us-west-2")
    private String description;

    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}

