package com.procreds.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "azure_aks_configs")
@CompoundIndex(name = "accountName_unique", def = "{'accountName': 1}", unique = true)
public class AzureAksConfig extends BaseEntity {

    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Account name can only contain letters, numbers, underscores, and hyphens")
    @Indexed(unique = true)
    private String accountName;

    @NotBlank(message = "Cluster name is required")
    @Size(min = 2, max = 100, message = "Cluster name must be between 2 and 100 characters")
    private String clusterName;

    @NotBlank(message = "Tenant ID is required")
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 
             message = "Tenant ID must be a valid UUID format")
    private String tenantId;

    @NotBlank(message = "Client ID is required")
    @Pattern(regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$", 
             message = "Client ID must be a valid UUID format")
    private String clientId;

    @NotBlank(message = "Client secret is required")
    @Size(min = 32, message = "Client secret must be at least 32 characters")
    private String clientSecret;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
}

