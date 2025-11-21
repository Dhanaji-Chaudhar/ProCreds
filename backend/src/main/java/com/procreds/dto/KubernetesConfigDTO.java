package com.procreds.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Kubernetes configuration data transfer object")
public class KubernetesConfigDTO {

    @Schema(description = "Unique identifier", example = "507f1f77bcf86cd799439011", accessMode = Schema.AccessMode.READ_ONLY)
    private String id;

    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Account name can only contain letters, numbers, underscores, and hyphens")
    @Schema(description = "Account name", example = "k8s-prod", required = true)
    private String accountName;

    @NotBlank(message = "Cluster name is required")
    @Size(min = 2, max = 100, message = "Cluster name must be between 2 and 100 characters")
    @Schema(description = "Kubernetes cluster name", example = "production-cluster", required = true)
    private String clusterName;

    @NotBlank(message = "Kubeconfig is required")
    @Size(min = 50, message = "Kubeconfig must be at least 50 characters")
    @Schema(description = "Kubernetes configuration (base64 encoded)", example = "YXBpVmVyc2lvbjogdjEKa2luZDogQ29uZmlnCmNsdXN0ZXJzOi4uLg==", required = true)
    private String kubeconfig;

    @Size(max = 50, message = "Namespace must be less than 50 characters")
    @Pattern(regexp = "^[a-z0-9]([-a-z0-9]*[a-z0-9])?$", message = "Namespace must be a valid Kubernetes namespace")
    @Schema(description = "Default namespace", example = "default")
    private String namespace;

    @Size(max = 50, message = "Region must be less than 50 characters")
    @Schema(description = "Cluster region", example = "us-west-2")
    private String region;

    @Size(max = 255, message = "Description must be less than 255 characters")
    @Schema(description = "Configuration description", example = "Production Kubernetes cluster")
    private String description;

    @Schema(description = "Creation timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}

