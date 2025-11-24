package com.procreds.controller;

import com.procreds.dto.GitLabConfigDTO;
import com.procreds.service.GitLabConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for GitLab configuration management
 * 
 * Provides RESTful endpoints for CRUD operations on GitLab configurations.
 * Includes comprehensive API documentation with Swagger annotations.
 * 
 * Base path: /gitlab
 * 
 * @author ProCreds Team
 */
@RestController
@RequestMapping("/gitlab")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "GitLab Configuration", description = "Manage GitLab platform credentials and settings")
public class GitLabConfigController {

    private final GitLabConfigService service;

    @Operation(
        summary = "Get all GitLab configurations",
        description = "Retrieve a paginated list of all GitLab configurations with optional search functionality"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved configurations"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<Page<GitLabConfigDTO>> getAllConfigurations(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Sort field", example = "accountName")
            @RequestParam(defaultValue = "accountName") String sortBy,
            
            @Parameter(description = "Sort direction", example = "asc")
            @RequestParam(defaultValue = "asc") String sortDir,
            
            @Parameter(description = "Search term for filtering results")
            @RequestParam(required = false) String search) {
        
        log.info("GET /gitlab - page: {}, size: {}, sortBy: {}, sortDir: {}, search: {}", 
                 page, size, sortBy, sortDir, search);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<GitLabConfigDTO> configurations;
        if (search != null && !search.trim().isEmpty()) {
            configurations = service.searchConfigurations(search.trim(), pageable);
        } else {
            configurations = service.getAllConfigurations(pageable);
        }

        return ResponseEntity.ok(configurations);
    }

    @Operation(
        summary = "Get GitLab configuration by ID",
        description = "Retrieve a specific GitLab configuration by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuration found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GitLabConfigDTO> getConfigurationById(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id) {
        
        log.info("GET /gitlab/{}", id);
        
        GitLabConfigDTO configuration = service.getConfigurationById(id);
        return ResponseEntity.ok(configuration);
    }

    @Operation(
        summary = "Get GitLab configuration by account name",
        description = "Retrieve a specific GitLab configuration by account name"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuration found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/account/{accountName}")
    public ResponseEntity<GitLabConfigDTO> getConfigurationByAccountName(
            @Parameter(description = "GitLab account name", required = true)
            @PathVariable String accountName) {
        
        log.info("GET /gitlab/account/{}", accountName);
        
        GitLabConfigDTO configuration = service.getConfigurationByAccountName(accountName);
        return ResponseEntity.ok(configuration);
    }

    @Operation(
        summary = "Create new GitLab configuration",
        description = "Create a new GitLab configuration with the provided details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Configuration created successfully",
            content = @Content(schema = @Schema(implementation = GitLabConfigDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "409", description = "Configuration with account name already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<GitLabConfigDTO> createConfiguration(
            @Parameter(description = "GitLab configuration details", required = true)
            @Valid @RequestBody GitLabConfigDTO dto) {
        
        log.info("POST /gitlab - Creating configuration for account: {}", dto.getAccountName());
        
        GitLabConfigDTO createdConfiguration = service.createConfiguration(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConfiguration);
    }

    @Operation(
        summary = "Update GitLab configuration",
        description = "Update an existing GitLab configuration with new details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Configuration updated successfully",
            content = @Content(schema = @Schema(implementation = GitLabConfigDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "409", description = "Account name conflicts with existing configuration"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<GitLabConfigDTO> updateConfiguration(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id,
            
            @Parameter(description = "Updated GitLab configuration details", required = true)
            @Valid @RequestBody GitLabConfigDTO dto) {
        
        log.info("PUT /gitlab/{} - Updating configuration for account: {}", id, dto.getAccountName());
        
        GitLabConfigDTO updatedConfiguration = service.updateConfiguration(id, dto);
        return ResponseEntity.ok(updatedConfiguration);
    }

    @Operation(
        summary = "Delete GitLab configuration",
        description = "Delete an existing GitLab configuration by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Configuration deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfiguration(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id) {
        
        log.info("DELETE /gitlab/{}", id);
        
        service.deleteConfiguration(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Check if account name exists",
        description = "Check if a GitLab configuration exists with the given account name"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/exists/{accountName}")
    public ResponseEntity<Boolean> existsByAccountName(
            @Parameter(description = "GitLab account name", required = true)
            @PathVariable String accountName) {
        
        log.info("GET /gitlab/exists/{}", accountName);
        
        boolean exists = service.existsByAccountName(accountName);
        return ResponseEntity.ok(exists);
    }

    @Operation(
        summary = "Test GitLab connection",
        description = "Test the connection to GitLab using the provided configuration"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connection test completed"),
        @ApiResponse(responseCode = "400", description = "Invalid configuration data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/test-connection")
    public ResponseEntity<?> testConnection(
            @Parameter(description = "GitLab configuration to test", required = true)
            @Valid @RequestBody GitLabConfigDTO dto) {
        
        log.info("POST /gitlab/test-connection - Testing connection for account: {}", dto.getAccountName());
        
        try {
            boolean isConnected = service.testConnection(dto);
            if (isConnected) {
                return ResponseEntity.ok(new TestConnectionResponse(true, "Connection successful", "GitLab API is accessible"));
            } else {
                return ResponseEntity.ok(new TestConnectionResponse(false, "Connection failed", "Unable to connect to GitLab API"));
            }
        } catch (Exception e) {
            log.error("Connection test failed for account: {}", dto.getAccountName(), e);
            return ResponseEntity.ok(new TestConnectionResponse(false, "Connection failed", e.getMessage()));
        }
    }

    /**
     * Response object for test connection endpoint
     */
    public static class TestConnectionResponse {
        private boolean success;
        private String message;
        private String details;

        public TestConnectionResponse(boolean success, String message, String details) {
            this.success = success;
            this.message = message;
            this.details = details;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getDetails() { return details; }
    }
}
