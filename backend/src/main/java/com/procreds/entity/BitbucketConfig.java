package com.procreds.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "bitbucket_configs")
@CompoundIndex(name = "username_unique", def = "{'username': 1}", unique = true)
public class BitbucketConfig extends BaseEntity {

    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Username can only contain letters, numbers, underscores, and hyphens")
    @Indexed(unique = true)
    private String username;

    @NotBlank(message = "App password is required")
    @Size(min = 10, message = "App password must be at least 10 characters")
    private String appPassword;

    @Size(max = 50, message = "Workspace name must be less than 50 characters")
    private String workspace;

    @Pattern(regexp = "^https?://.+", message = "API URL must be a valid HTTP or HTTPS URL")
    private String apiUrl;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
}
