package com.procreds.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "gcp_gke_configs")
@CompoundIndex(name = "accountName_unique", def = "{'accountName': 1}", unique = true)
public class GcpGkeConfig extends BaseEntity {

    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Account name can only contain letters, numbers, underscores, and hyphens")
    @Indexed(unique = true)
    private String accountName;

    @NotBlank(message = "Cluster name is required")
    @Size(min = 2, max = 100, message = "Cluster name must be between 2 and 100 characters")
    private String clusterName;

    @NotBlank(message = "Service account JSON is required")
    @Size(min = 100, message = "Service account JSON must be at least 100 characters")
    private String serviceAccountJson;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
}

