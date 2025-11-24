package com.procreds.controller;

import com.procreds.dto.AwsEksConfigDTO;
import com.procreds.service.AwsEksConfigService;
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
 * REST Controller for AWS EKS configuration management
 * 
 * Provides RESTful endpoints for CRUD operations on AWS EKS configurations.
 * Includes comprehensive API documentation with Swagger annotations.
 * 
 * Base path: /aws-eks
 * 
 * @author ProCreds Team
 */
@RestController
@RequestMapping("/aws-eks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AWS EKS Configuration", description = "Manage AWS EKS platform credentials and settings")
public class AwsEksConfigController {

    private final AwsEksConfigService service;

    @Operation(
        summary = "Get all AWS EKS configurations",
        description = "Retrieve a paginated list of all AWS EKS configurations with optional search functionality"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved configurations"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<Page<AwsEksConfigDTO>> getAllConfigurations(
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
        
        log.info("GET /aws-eks - page: {}, size: {}, sortBy: {}, sortDir: {}, search: {}", 
                 page, size, sortBy, sortDir, search);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AwsEksConfigDTO> configurations;
        if (search != null && !search.trim().isEmpty()) {
            configurations = service.searchConfigurations(search.trim(), pageable);
        } else {
            configurations = service.getAllConfigurations(pageable);
        }

        return ResponseEntity.ok(configurations);
    }

    @Operation(
        summary = "Get AWS EKS configuration by ID",
        description = "Retrieve a specific AWS EKS configuration by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuration found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AwsEksConfigDTO> getConfigurationById(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id) {
        
        log.info("GET /aws-eks/{}", id);
        
        AwsEksConfigDTO configuration = service.getConfigurationById(id);
        return ResponseEntity.ok(configuration);
    }

    @Operation(
        summary = "Get AWS EKS configuration by account name",
        description = "Retrieve a specific AWS EKS configuration by account name"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Configuration found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/account/{accountName}")
    public ResponseEntity<AwsEksConfigDTO> getConfigurationByAccountName(
            @Parameter(description = "AWS EKS account name", required = true)
            @PathVariable String accountName) {
        
        log.info("GET /aws-eks/account/{}", accountName);
        
        AwsEksConfigDTO configuration = service.getConfigurationByAccountName(accountName);
        return ResponseEntity.ok(configuration);
    }

    @Operation(
        summary = "Create new AWS EKS configuration",
        description = "Create a new AWS EKS configuration with the provided details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Configuration created successfully",
            content = @Content(schema = @Schema(implementation = AwsEksConfigDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "409", description = "Configuration with account name already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<AwsEksConfigDTO> createConfiguration(
            @Parameter(description = "AWS EKS configuration details", required = true)
            @Valid @RequestBody AwsEksConfigDTO dto) {
        
        log.info("POST /aws-eks - Creating configuration for account: {}", dto.getAccountName());
        
        AwsEksConfigDTO createdConfiguration = service.createConfiguration(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdConfiguration);
    }

    @Operation(
        summary = "Update AWS EKS configuration",
        description = "Update an existing AWS EKS configuration with new details"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Configuration updated successfully",
            content = @Content(schema = @Schema(implementation = AwsEksConfigDTO.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "404", description = "Configuration not found"),
        @ApiResponse(responseCode = "409", description = "Account name conflicts with existing configuration"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AwsEksConfigDTO> updateConfiguration(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id,
            
            @Parameter(description = "Updated AWS EKS configuration details", required = true)
            @Valid @RequestBody AwsEksConfigDTO dto) {
        
        log.info("PUT /aws-eks/{} - Updating configuration for account: {}", id, dto.getAccountName());
        
        AwsEksConfigDTO updatedConfiguration = service.updateConfiguration(id, dto);
        return ResponseEntity.ok(updatedConfiguration);
    }

    @Operation(
        summary = "Delete AWS EKS configuration",
        description = "Delete an existing AWS EKS configuration by its ID"
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
        
        log.info("DELETE /aws-eks/{}", id);
        
        service.deleteConfiguration(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Check if account name exists",
        description = "Check if an AWS EKS configuration exists with the given account name"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/exists/{accountName}")
    public ResponseEntity<Boolean> existsByAccountName(
            @Parameter(description = "AWS EKS account name", required = true)
            @PathVariable String accountName) {
        
        log.info("GET /aws-eks/exists/{}", accountName);
        
        boolean exists = service.existsByAccountName(accountName);
        return ResponseEntity.ok(exists);
    }
}

