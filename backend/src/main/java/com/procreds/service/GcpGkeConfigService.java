package com.procreds.service;

import com.procreds.dto.GcpGkeConfigDTO;
import com.procreds.entity.GcpGkeConfig;
import com.procreds.exception.ResourceAlreadyExistsException;
import com.procreds.exception.ResourceNotFoundException;
import com.procreds.repository.GcpGkeConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for GCP GKE configuration management
 * 
 * Provides business logic for CRUD operations on GCP GKE configurations.
 * Handles validation, error handling, and data transformation between
 * DTOs and entities.
 * 
 * @author ProCreds Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GcpGkeConfigService {

    private final GcpGkeConfigRepository repository;

    /**
     * Retrieve all GCP GKE configurations with pagination
     * 
     * @param pageable pagination information
     * @return Page of GCP GKE configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<GcpGkeConfigDTO> getAllConfigurations(Pageable pageable) {
        log.debug("Retrieving all GCP GKE configurations with pagination: {}", pageable);
        
        Page<GcpGkeConfig> entities = repository.findAllByOrderByAccountNameAsc(pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Retrieve GCP GKE configuration by ID
     * 
     * @param id the configuration ID
     * @return GCP GKE configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public GcpGkeConfigDTO getConfigurationById(String id) {
        log.debug("Retrieving GCP GKE configuration by ID: {}", id);
        
        GcpGkeConfig entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("GCP GKE configuration not found with ID: " + id));
        
        return convertToDTO(entity);
    }

    /**
     * Retrieve GCP GKE configuration by account name
     * 
     * @param accountName the GCP GKE account name
     * @return GCP GKE configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public GcpGkeConfigDTO getConfigurationByAccountName(String accountName) {
        log.debug("Retrieving GCP GKE configuration by account name: {}", accountName);
        
        GcpGkeConfig entity = repository.findByAccountName(accountName)
            .orElseThrow(() -> new ResourceNotFoundException("GCP GKE configuration not found with account name: " + accountName));
        
        return convertToDTO(entity);
    }

    /**
     * Create a new GCP GKE configuration
     * 
     * @param dto the GCP GKE configuration DTO
     * @return created GCP GKE configuration DTO
     * @throws ResourceAlreadyExistsException if account name already exists
     */
    public GcpGkeConfigDTO createConfiguration(GcpGkeConfigDTO dto) {
        log.info("Creating new GCP GKE configuration for account: {}", dto.getAccountName());
        
        // Check if account name already exists
        if (repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("GCP GKE configuration already exists with account name: " + dto.getAccountName());
        }
        
        GcpGkeConfig entity = convertToEntity(dto);
        GcpGkeConfig savedEntity = repository.save(entity);
        
        log.info("Successfully created GCP GKE configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Update an existing GCP GKE configuration
     * 
     * @param id the configuration ID
     * @param dto the updated GCP GKE configuration DTO
     * @return updated GCP GKE configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     * @throws ResourceAlreadyExistsException if account name conflicts with existing configuration
     */
    public GcpGkeConfigDTO updateConfiguration(String id, GcpGkeConfigDTO dto) {
        log.info("Updating GCP GKE configuration with ID: {}", id);
        
        GcpGkeConfig existingEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("GCP GKE configuration not found with ID: " + id));
        
        // Check if account name conflicts with another configuration
        if (!existingEntity.getAccountName().equals(dto.getAccountName()) && 
            repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("GCP GKE configuration already exists with account name: " + dto.getAccountName());
        }
        
        // Update entity fields
        existingEntity.setAccountName(dto.getAccountName());
        existingEntity.setClusterName(dto.getClusterName());
        existingEntity.setServiceAccountJson(dto.getServiceAccountJson());
        existingEntity.setDescription(dto.getDescription());
        
        GcpGkeConfig savedEntity = repository.save(existingEntity);
        
        log.info("Successfully updated GCP GKE configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Delete a GCP GKE configuration
     * 
     * @param id the configuration ID
     * @throws ResourceNotFoundException if configuration not found
     */
    public void deleteConfiguration(String id) {
        log.info("Deleting GCP GKE configuration with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("GCP GKE configuration not found with ID: " + id);
        }
        
        repository.deleteById(id);
        log.info("Successfully deleted GCP GKE configuration with ID: {}", id);
    }

    /**
     * Search GCP GKE configurations across multiple fields
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return Page of matching GCP GKE configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<GcpGkeConfigDTO> searchConfigurations(String searchTerm, Pageable pageable) {
        log.debug("Searching GCP GKE configurations with term: {}", searchTerm);
        
        Page<GcpGkeConfig> entities = repository.searchConfigurations(searchTerm, pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Check if a GCP GKE configuration exists with the given account name
     * 
     * @param accountName the GCP GKE account name
     * @return true if configuration exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByAccountName(String accountName) {
        return repository.existsByAccountName(accountName);
    }

    /**
     * Test connection to GCP GKE using the provided configuration
     * 
     * @param dto the GCP GKE configuration DTO to test
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection(GcpGkeConfigDTO dto) {
        log.debug("Testing GCP GKE connection for account: {}", dto.getAccountName());
        
        try {
            // TODO: Implement actual GCP GKE API connection test
            // For now, return true if required fields are present
            boolean isValid = dto.getAccountName() != null && !dto.getAccountName().trim().isEmpty() &&
                             dto.getClusterName() != null && !dto.getClusterName().trim().isEmpty() &&
                             dto.getServiceAccountJson() != null && !dto.getServiceAccountJson().trim().isEmpty();
            
            log.info("GCP GKE connection test for account '{}': {}", 
                    dto.getAccountName(), isValid ? "SUCCESS" : "FAILED");
            return isValid;
            
        } catch (Exception e) {
            log.error("GCP GKE connection test failed for account '{}': {}", 
                     dto.getAccountName(), e.getMessage());
            return false;
        }
    }

    /**
     * Convert GCP GKE entity to DTO
     * 
     * @param entity the GCP GKE configuration entity
     * @return GCP GKE configuration DTO
     */
    private GcpGkeConfigDTO convertToDTO(GcpGkeConfig entity) {
        GcpGkeConfigDTO dto = new GcpGkeConfigDTO();
        dto.setId(entity.getId());
        dto.setAccountName(entity.getAccountName());
        dto.setClusterName(entity.getClusterName());
        dto.setServiceAccountJson(entity.getServiceAccountJson());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * Convert GCP GKE DTO to entity
     * 
     * @param dto the GCP GKE configuration DTO
     * @return GCP GKE configuration entity
     */
    private GcpGkeConfig convertToEntity(GcpGkeConfigDTO dto) {
        GcpGkeConfig entity = new GcpGkeConfig();
        entity.setAccountName(dto.getAccountName());
        entity.setClusterName(dto.getClusterName());
        entity.setServiceAccountJson(dto.getServiceAccountJson());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
