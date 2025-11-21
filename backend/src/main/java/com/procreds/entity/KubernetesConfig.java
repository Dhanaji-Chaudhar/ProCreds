package com.procreds.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "kubernetes_configs")
@CompoundIndex(name = "accountName_unique", def = "{'accountName': 1}", unique = true)
public class KubernetesConfig extends BaseEntity {

    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Account name can only contain letters, numbers, underscores, and hyphens")
    @Indexed(unique = true)
    private String accountName;

    @NotBlank(message = "Cluster name is required")
    @Size(min = 2, max = 100, message = "Cluster name must be between 2 and 100 characters")
    private String clusterName;

    @NotBlank(message = "Kubeconfig is required")
    @Size(min = 50, message = "Kubeconfig must be at least 50 characters")
    private String kubeconfig;

    @Size(max = 50, message = "Namespace must be less than 50 characters")
    @Pattern(regexp = "^[a-z0-9]([-a-z0-9]*[a-z0-9])?$", message = "Namespace must be a valid Kubernetes namespace")
    private String namespace;

    @Size(max = 50, message = "Region must be less than 50 characters")
    private String region;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
}

