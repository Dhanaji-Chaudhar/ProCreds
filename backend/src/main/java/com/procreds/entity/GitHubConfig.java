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
 * Entity representing GitHub platform configuration
 * 
 * Stores GitHub account credentials and settings including:
 * - Account name (unique identifier)
 * - Personal access token for API authentication
 * - Organization name (optional)
 * - Custom API URL (optional, defaults to github.com)
 * - Description for identification purposes
 * 
 * Collection: github_configs
 * Indexes: accountName (unique), organization, createdAt
 * 
 * @author ProCreds Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "github_configs")
public class GitHubConfig extends BaseEntity {

    /**
     * GitHub account name - serves as unique identifier
     * Must be 2-50 characters, alphanumeric with hyphens allowed
     */
    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Account name must contain only alphanumeric characters and hyphens")
    @Indexed(unique = true)
    @Field("account_name")
    private String accountName;

    /**
     * GitHub personal access token for API authentication
     * Must be at least 10 characters (GitHub tokens are typically 40+ chars)
     */
    @NotBlank(message = "Access token is required")
    @Size(min = 10, message = "Access token must be at least 10 characters")
    @Field("access_token")
    private String accessToken;

    /**
     * GitHub organization name (optional)
     * If provided, operations will be scoped to this organization
     */
    @Size(max = 50, message = "Organization name cannot exceed 50 characters")
    @Indexed
    @Field("organization")
    private String organization;

    /**
     * Custom GitHub API URL (optional)
     * Useful for GitHub Enterprise installations
     * Defaults to https://api.github.com if not provided
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

