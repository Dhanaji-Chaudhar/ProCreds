package com.procreds.service;

import com.procreds.dto.AzureAksConfigDTO;
import com.procreds.entity.AzureAksConfig;
import com.procreds.exception.ResourceAlreadyExistsException;
import com.procreds.exception.ResourceNotFoundException;
import com.procreds.repository.AzureAksConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for Azure AKS configuration management
 * 
 * Provides business logic for CRUD operations on Azure AKS configurations.
 * Handles validation, error handling, and data transformation between
 * DTOs and entities.
 * 
 * @author ProCreds Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AzureAksConfigService {

    private final AzureAksConfigRepository repository;

    /**
     * Retrieve all Azure AKS configurations with pagination
     * 
     * @param pageable pagination information
     * @return Page of Azure AKS configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<AzureAksConfigDTO> getAllConfigurations(Pageable pageable) {
        log.debug("Retrieving all Azure AKS configurations with pagination: {}", pageable);
        
        Page<AzureAksConfig> entities = repository.findAllByOrderByAccountNameAsc(pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Retrieve Azure AKS configuration by ID
     * 
     * @param id the configuration ID
     * @return Azure AKS configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public AzureAksConfigDTO getConfigurationById(String id) {
        log.debug("Retrieving Azure AKS configuration by ID: {}", id);
        
        AzureAksConfig entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Azure AKS configuration not found with ID: " + id));
        
        return convertToDTO(entity);
    }

    /**
     * Retrieve Azure AKS configuration by account name
     * 
     * @param accountName the Azure AKS account name
     * @return Azure AKS configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public AzureAksConfigDTO getConfigurationByAccountName(String accountName) {
        log.debug("Retrieving Azure AKS configuration by account name: {}", accountName);
        
        AzureAksConfig entity = repository.findByAccountName(accountName)
            .orElseThrow(() -> new ResourceNotFoundException("Azure AKS configuration not found with account name: " + accountName));
        
        return convertToDTO(entity);
    }

    /**
     * Create a new Azure AKS configuration
     * 
     * @param dto the Azure AKS configuration DTO
     * @return created Azure AKS configuration DTO
     * @throws ResourceAlreadyExistsException if account name already exists
     */
    public AzureAksConfigDTO createConfiguration(AzureAksConfigDTO dto) {
        log.info("Creating new Azure AKS configuration for account: {}", dto.getAccountName());
        
        // Check if account name already exists
        if (repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("Azure AKS configuration already exists with account name: " + dto.getAccountName());
        }
        
        AzureAksConfig entity = convertToEntity(dto);
        AzureAksConfig savedEntity = repository.save(entity);
        
        log.info("Successfully created Azure AKS configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Update an existing Azure AKS configuration
     * 
     * @param id the configuration ID
     * @param dto the updated Azure AKS configuration DTO
     * @return updated Azure AKS configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     * @throws ResourceAlreadyExistsException if account name conflicts with existing configuration
     */
    public AzureAksConfigDTO updateConfiguration(String id, AzureAksConfigDTO dto) {
        log.info("Updating Azure AKS configuration with ID: {}", id);
        
        AzureAksConfig existingEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Azure AKS configuration not found with ID: " + id));
        
        // Check if account name conflicts with another configuration
        if (!existingEntity.getAccountName().equals(dto.getAccountName()) && 
            repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("Azure AKS configuration already exists with account name: " + dto.getAccountName());
        }
        
        // Update entity fields
        existingEntity.setAccountName(dto.getAccountName());
        existingEntity.setClusterName(dto.getClusterName());
        existingEntity.setTenantId(dto.getTenantId());
        existingEntity.setClientId(dto.getClientId());
        existingEntity.setClientSecret(dto.getClientSecret());
        existingEntity.setDescription(dto.getDescription());
        
        AzureAksConfig savedEntity = repository.save(existingEntity);
        
        log.info("Successfully updated Azure AKS configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Delete an Azure AKS configuration
     * 
     * @param id the configuration ID
     * @throws ResourceNotFoundException if configuration not found
     */
    public void deleteConfiguration(String id) {
        log.info("Deleting Azure AKS configuration with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Azure AKS configuration not found with ID: " + id);
        }
        
        repository.deleteById(id);
        log.info("Successfully deleted Azure AKS configuration with ID: {}", id);
    }

    /**
     * Search Azure AKS configurations across multiple fields
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return Page of matching Azure AKS configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<AzureAksConfigDTO> searchConfigurations(String searchTerm, Pageable pageable) {
        log.debug("Searching Azure AKS configurations with term: {}", searchTerm);
        
        Page<AzureAksConfig> entities = repository.searchConfigurations(searchTerm, pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Check if an Azure AKS configuration exists with the given account name
     * 
     * @param accountName the Azure AKS account name
     * @return true if configuration exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByAccountName(String accountName) {
        return repository.existsByAccountName(accountName);
    }

    /**
     * Test connection to Azure AKS using the provided configuration
     * 
     * @param dto the Azure AKS configuration DTO to test
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection(AzureAksConfigDTO dto) {
        log.debug("Testing Azure AKS connection for account: {}", dto.getAccountName());
        
        try {
            // TODO: Implement actual Azure AKS API connection test
            // For now, return true if required fields are present
            boolean isValid = dto.getAccountName() != null && !dto.getAccountName().trim().isEmpty() &&
                             dto.getClusterName() != null && !dto.getClusterName().trim().isEmpty() &&
                             dto.getTenantId() != null && !dto.getTenantId().trim().isEmpty() &&
                             dto.getClientId() != null && !dto.getClientId().trim().isEmpty() &&
                             dto.getClientSecret() != null && !dto.getClientSecret().trim().isEmpty();
            
            log.info("Azure AKS connection test for account '{}': {}", 
                    dto.getAccountName(), isValid ? "SUCCESS" : "FAILED");
            return isValid;
            
        } catch (Exception e) {
            log.error("Azure AKS connection test failed for account '{}': {}", 
                     dto.getAccountName(), e.getMessage());
            return false;
        }
    }

    /**
     * Convert Azure AKS entity to DTO
     * 
     * @param entity the Azure AKS configuration entity
     * @return Azure AKS configuration DTO
     */
    private AzureAksConfigDTO convertToDTO(AzureAksConfig entity) {
        AzureAksConfigDTO dto = new AzureAksConfigDTO();
        dto.setId(entity.getId());
        dto.setAccountName(entity.getAccountName());
        dto.setClusterName(entity.getClusterName());
        dto.setTenantId(entity.getTenantId());
        dto.setClientId(entity.getClientId());
        dto.setClientSecret(entity.getClientSecret());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * Convert Azure AKS DTO to entity
     * 
     * @param dto the Azure AKS configuration DTO
     * @return Azure AKS configuration entity
     */
    private AzureAksConfig convertToEntity(AzureAksConfigDTO dto) {
        AzureAksConfig entity = new AzureAksConfig();
        entity.setAccountName(dto.getAccountName());
        entity.setClusterName(dto.getClusterName());
        entity.setTenantId(dto.getTenantId());
        entity.setClientId(dto.getClientId());
        entity.setClientSecret(dto.getClientSecret());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
