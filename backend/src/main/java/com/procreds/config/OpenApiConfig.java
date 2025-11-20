package com.procreds.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3 configuration for ProCreds API documentation
 * 
 * Configures:
 * - API information and metadata
 * - HTTP Basic Authentication scheme
 * - Server information
 * - Contact and license details
 * 
 * @author ProCreds Team
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ProCreds API")
                .version("1.0.0")
                .description("""
                    # DevOps Platform Credential Manager API
                    
                    A comprehensive REST API for managing DevOps platform credentials and configurations.
                    
                    ## Supported Platforms
                    - **GitHub**: Account credentials and organization settings
                    - **Bitbucket**: Username and app password management
                    - **GitLab**: Personal access token configuration
                    - **Jenkins**: Server URL and API token management
                    - **Jira**: Project configuration and API credentials
                    - **SonarQube**: Server and organization settings
                    - **Kubernetes**: Generic cluster configurations
                    - **AWS EKS**: Amazon Elastic Kubernetes Service
                    - **Azure AKS**: Azure Kubernetes Service
                    - **GCP GKE**: Google Kubernetes Engine
                    
                    ## Features
                    - Full CRUD operations for all platforms
                    - Search and pagination support
                    - Input validation and error handling
                    - HTTP Basic Authentication
                    - MongoDB persistence with auditing
                    
                    ## Authentication
                    This API uses HTTP Basic Authentication. Include your credentials in the Authorization header:
                    ```
                    Authorization: Basic <base64-encoded-credentials>
                    ```
                    
                    Default credentials: `admin:admin123`
                    """)
                .contact(new Contact()
                    .name("ProCreds Team")
                    .email("support@procreds.com")
                    .url("https://github.com/Dhanaji-Chaudhar/ProCreds"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080/api")
                    .description("Development server"),
                new Server()
                    .url("https://api.procreds.com/api")
                    .description("Production server")))
            .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
            .components(new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes("basicAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("basic")
                    .description("HTTP Basic Authentication with username and password")));
    }
}

