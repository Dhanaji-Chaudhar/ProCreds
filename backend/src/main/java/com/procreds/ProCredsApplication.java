package com.procreds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Main application class for ProCreds - DevOps Platform Credential Manager
 * 
 * This application provides a comprehensive solution for managing DevOps platform
 * credentials and configurations across multiple platforms including GitHub, Bitbucket,
 * GitLab, Jenkins, Jira, SonarQube, and various Kubernetes platforms.
 * 
 * Features:
 * - RESTful API with OpenAPI 3 documentation
 * - MongoDB integration with auditing
 * - Spring Security with HTTP Basic Authentication
 * - Comprehensive validation and error handling
 * - CORS support for frontend integration
 * 
 * @author ProCreds Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableMongoAuditing
public class ProCredsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProCredsApplication.class, args);
    }
}

