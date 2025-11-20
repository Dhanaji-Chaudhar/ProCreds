package com.procreds.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Entity representing Bitbucket platform configuration
 * 
 * Stores Bitbucket account credentials and settings including:
 * - Username (unique identifier)
 * - App password for API authentication
 * - Workspace name (optional)
 * - Custom API URL (optional, defaults to bitbucket.org)
 * - Description for identification purposes
 * 
 * Collection: bitbucket_configs
 * Indexes: username (unique), workspace, createdAt
 * 
 * @author ProCreds Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "bitbucket_configs")
public class BitbucketConfig extends BaseEntity {

    /**
     * Bitbucket username - serves as unique identifier
     * Must be 2-50 characters, alphanumeric with underscores and hyphens allowed
     */
    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Username must contain only alphanumeric characters, underscores, and hyphens")
    @Indexed(unique = true)
    @Field("username")
    private String username;

    /**
     * Bitbucket app password for API authentication
     * Must be at least 10 characters
     */
    @NotBlank(message = "App password is required")
    @Size(min = 10, message = "App password must be at least 10 characters")
    @Field("app_password")
    private String appPassword;

    /**
     * Bitbucket workspace name (optional)
     * If provided, operations will be scoped to this workspace
     */
    @Size(max = 50, message = "Workspace name cannot exceed 50 characters")
    @Indexed
    @Field("workspace")
    private String workspace;

    /**
     * Custom Bitbucket API URL (optional)
     * Useful for Bitbucket Server installations
     * Defaults to https://api.bitbucket.org if not provided
     */
    @Pattern(regexp = "^https?://.+", message = "API URL must be a valid HTTP/HTTPS URL")
    @Field("api_url")
    private String apiUrl;

    /**
     * Human-readable description for this configuration
     * Helps identify the purpose or context of this credential set
     */
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Field("description")
    private String description;
}

