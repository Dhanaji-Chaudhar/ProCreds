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
 * Entity representing GitLab platform configuration
 * 
 * Stores GitLab account credentials and settings including:
 * - Account name (unique identifier)
 * - Personal access token for API authentication
 * - Group ID (optional)
 * - Custom API URL (optional, defaults to gitlab.com)
 * - Description for identification purposes
 * 
 * Collection: gitlab_configs
 * Indexes: accountName (unique), groupId, createdAt
 * 
 * @author ProCreds Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "gitlab_configs")
public class GitLabConfig extends BaseEntity {

    /**
     * GitLab account name - serves as unique identifier
     * Must be 2-50 characters, alphanumeric with hyphens and underscores allowed
     */
    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Account name must contain only alphanumeric characters, underscores, and hyphens")
    @Indexed(unique = true)
    @Field("account_name")
    private String accountName;

    /**
     * GitLab personal access token for API authentication
     * Must be at least 10 characters (GitLab tokens are typically 20+ chars)
     */
    @NotBlank(message = "Personal access token is required")
    @Size(min = 10, message = "Personal access token must be at least 10 characters")
    @Field("personal_access_token")
    private String personalAccessToken;

    /**
     * GitLab group ID (optional)
     * If provided, operations will be scoped to this group
     */
    @Pattern(regexp = "^[0-9]+$", message = "Group ID must be numeric")
    @Indexed
    @Field("group_id")
    private String groupId;

    /**
     * Custom GitLab API URL (optional)
     * Useful for GitLab self-hosted installations
     * Defaults to https://gitlab.com/api/v4 if not provided
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

