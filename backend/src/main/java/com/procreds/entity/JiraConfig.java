package com.procreds.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "jira_configs")
@CompoundIndex(name = "accountName_unique", def = "{'accountName': 1}", unique = true)
public class JiraConfig extends BaseEntity {

    @NotBlank(message = "Account name is required")
    @Size(min = 2, max = 50, message = "Account name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9_-]*[a-zA-Z0-9]$|^[a-zA-Z0-9]$", 
             message = "Account name can only contain letters, numbers, underscores, and hyphens")
    @Indexed(unique = true)
    private String accountName;

    @NotBlank(message = "Base URL is required")
    @Pattern(regexp = "^https?://.+", message = "Base URL must be a valid HTTP or HTTPS URL")
    private String baseUrl;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotBlank(message = "API token is required")
    @Size(min = 10, message = "API token must be at least 10 characters")
    private String apiToken;

    @Pattern(regexp = "^[A-Z0-9]+$", message = "Project key must contain only uppercase letters and numbers")
    private String projectKey;

    @Size(max = 50, message = "Issue type default must be less than 50 characters")
    private String issueTypeDefault;

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
}

