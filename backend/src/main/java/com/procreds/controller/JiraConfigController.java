package com.procreds.controller;

import com.procreds.dto.JiraConfigDTO;
import com.procreds.service.JiraConfigService;
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
 * REST Controller for Jira configuration management
 * 
 * Provides RESTful endpoints for CRUD operations on Jira configurations.
 * Includes comprehensive API documentation with Swagger annotations.
 * 
 * Base path: /jira
 * 
 * @author ProCreds Team
 */
@RestController
@RequestMapping("/jira")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Jira Configuration", description = "Manage Jira platform credentials and settings")
public class JiraConfigController {

    private final JiraConfigService service;

    @Operation(
        summary = "Get all Jira configurations",
        description = "Retrieve a paginated list of all Jira configurations with optional search functionality"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved configurations"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<Page<JiraConfigDTO>> getAllConfigurations(
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
        
        log.info("GET /jira - page: {}, size: {}, sortBy: {}, sortDir: {}, search: {}", 
                 page, size, sortBy, sortDir, search);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<JiraConfigDTO> configurations;
        if (search != null && !search.trim().isEmpty()) {
            configurations = service.searchConfigurations(search.trim(), pageable);
        } else {
            configurations = service.getAllConfigurations(pageable);
        }

        return ResponseEntity.ok(configurations);
    }

    @Operation(
        summary = "Get Jira configuration by ID",
        description = "Retrieve a specific Jira configuration by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuration found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<JiraConfigDTO> getConfigurationById(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id) {
        
        log.info("GET /jira/{}", id);
        
        JiraConfigDTO configuration = service.getConfigurationById(id);
        return ResponseEntity.ok(configuration);
    }

    @Operation(
        summary = "Get Jira configuration by account name",
        description = "Retrieve a specific Jira configuration by account name"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuration found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/account/{accountName}")
    public ResponseEntity<JiraConfigDTO> getConfigurationByAccountName(
            @Parameter(description = "Jira account name", required = true)
            @PathVariable String accountName) {
        
        log.info("GET /jira/account/{}", accountName);
        
        JiraConfigDTO configuration = service.getConfigurationByAccountName(accountName);
        return ResponseEntity.ok(configuration);
    }

    @Operation(
        summary = "Create new Jira configuration",
        description = "Create a new Jira configuration with the provided details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Configuration created successfully",
            content = @Content(schema = @Schema(implementation = JiraConfigDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "409", description = "Configuration with account name already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<JiraConfigDTO> createConfiguration(
            @Parameter(description = "Jira configuration details", required = true)
            @Valid @RequestBody JiraConfigDTO dto) {
        
        log.info("POST /jira - Creating configuration for account: {}", dto.getAccountName());
        
        JiraConfigDTO createdConfiguration = service.createConfiguration(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConfiguration);
    }

    @Operation(
        summary = "Update Jira configuration",
        description = "Update an existing Jira configuration with new details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Configuration updated successfully",
            content = @Content(schema = @Schema(implementation = JiraConfigDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "409", description = "Account name conflicts with existing configuration"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<JiraConfigDTO> updateConfiguration(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id,
            
            @Parameter(description = "Updated Jira configuration details", required = true)
            @Valid @RequestBody JiraConfigDTO dto) {
        
        log.info("PUT /jira/{} - Updating configuration for account: {}", id, dto.getAccountName());
        
        JiraConfigDTO updatedConfiguration = service.updateConfiguration(id, dto);
        return ResponseEntity.ok(updatedConfiguration);
    }

    @Operation(
        summary = "Delete Jira configuration",
        description = "Delete an existing Jira configuration by its ID"
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
        
        log.info("DELETE /jira/{}", id);
        
        service.deleteConfiguration(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Check if account name exists",
        description = "Check if a Jira configuration exists with the given account name"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/exists/{accountName}")
    public ResponseEntity<Boolean> existsByAccountName(
            @Parameter(description = "Jira account name", required = true)
            @PathVariable String accountName) {
        
        log.info("GET /jira/exists/{}", accountName);
        
        boolean exists = service.existsByAccountName(accountName);
        return ResponseEntity.ok(exists);
    }
}

