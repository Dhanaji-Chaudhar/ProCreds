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
 * Entity representing Jenkins platform configuration
 * 
 * Stores Jenkins server credentials and settings including:
 * - Account name (unique identifier)
 * - Base URL of Jenkins server
 * - Username for authentication
 * - API token for secure access
 * - Job prefix for organization (optional)
 * - Description for identification purposes
 * 
 * Collection: jenkins_configs
 * Indexes: accountName (unique), baseUrl, username, createdAt
 * 
 * @author ProCreds Team
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "jenkins_configs")
public class JenkinsConfig extends BaseEntity {

    /**
     * Jenkins account/configuration name - serves as unique identifier
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
     * Jenkins server base URL
     * Must be a valid HTTP/HTTPS URL
     */
    @NotBlank(message = "Base URL is required")
    @Pattern(regexp = "^https?://.+", message = "Base URL must be a valid HTTP/HTTPS URL")
    @Indexed
    @Field("base_url")
    private String baseUrl;

    /**
     * Jenkins username for authentication
     * Must be 2-50 characters
     */
    @NotBlank(message = "Username is required")
    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    @Indexed
    @Field("username")
    private String username;

    /**
     * Jenkins API token for secure authentication
     * Must be at least 10 characters
     */
    @NotBlank(message = "API token is required")
    @Size(min = 10, message = "API token must be at least 10 characters")
    @Field("api_token")
    private String apiToken;

    /**
     * Job prefix for organizing Jenkins jobs (optional)
     * Useful for multi-tenant or organized Jenkins environments
     */
    @Size(max = 50, message = "Job prefix cannot exceed 50 characters")
    @Field("job_prefix")
    private String jobPrefix;

    /**
     * Human-readable description for this configuration
     * Helps identify the purpose or context of this credential set
     */
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Field("description")
    private String description;
}

