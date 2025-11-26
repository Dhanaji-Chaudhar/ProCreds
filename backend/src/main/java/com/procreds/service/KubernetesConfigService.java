package com.procreds.service;

import com.procreds.dto.KubernetesConfigDTO;
import com.procreds.entity.KubernetesConfig;
import com.procreds.exception.ResourceAlreadyExistsException;
import com.procreds.exception.ResourceNotFoundException;
import com.procreds.repository.KubernetesConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for Kubernetes configuration management
 * 
 * Provides business logic for CRUD operations on Kubernetes configurations.
 * Handles validation, error handling, and data transformation between
 * DTOs and entities.
 * 
 * @author ProCreds Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class KubernetesConfigService {

    private final KubernetesConfigRepository repository;

    /**
     * Retrieve all Kubernetes configurations with pagination
     * 
     * @param pageable pagination information
     * @return Page of Kubernetes configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<KubernetesConfigDTO> getAllConfigurations(Pageable pageable) {
        log.debug("Retrieving all Kubernetes configurations with pagination: {}", pageable);
        
        Page<KubernetesConfig> entities = repository.findAllByOrderByAccountNameAsc(pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Retrieve Kubernetes configuration by ID
     * 
     * @param id the configuration ID
     * @return Kubernetes configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public KubernetesConfigDTO getConfigurationById(String id) {
        log.debug("Retrieving Kubernetes configuration by ID: {}", id);
        
        KubernetesConfig entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Kubernetes configuration not found with ID: " + id));
        
        return convertToDTO(entity);
    }

    /**
     * Retrieve Kubernetes configuration by account name
     * 
     * @param accountName the Kubernetes account name
     * @return Kubernetes configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public KubernetesConfigDTO getConfigurationByAccountName(String accountName) {
        log.debug("Retrieving Kubernetes configuration by account name: {}", accountName);
        
        KubernetesConfig entity = repository.findByAccountName(accountName)
            .orElseThrow(() -> new ResourceNotFoundException("Kubernetes configuration not found with account name: " + accountName));
        
        return convertToDTO(entity);
    }

    /**
     * Create a new Kubernetes configuration
     * 
     * @param dto the Kubernetes configuration DTO
     * @return created Kubernetes configuration DTO
     * @throws ResourceAlreadyExistsException if account name already exists
     */
    public KubernetesConfigDTO createConfiguration(KubernetesConfigDTO dto) {
        log.info("Creating new Kubernetes configuration for account: {}", dto.getAccountName());
        
        // Check if account name already exists
        if (repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("Kubernetes configuration already exists with account name: " + dto.getAccountName());
        }
        
        KubernetesConfig entity = convertToEntity(dto);
        KubernetesConfig savedEntity = repository.save(entity);
        
        log.info("Successfully created Kubernetes configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Update an existing Kubernetes configuration
     * 
     * @param id the configuration ID
     * @param dto the updated Kubernetes configuration DTO
     * @return updated Kubernetes configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     * @throws ResourceAlreadyExistsException if account name conflicts with existing configuration
     */
    public KubernetesConfigDTO updateConfiguration(String id, KubernetesConfigDTO dto) {
        log.info("Updating Kubernetes configuration with ID: {}", id);
        
        KubernetesConfig existingEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Kubernetes configuration not found with ID: " + id));
        
        // Check if account name conflicts with another configuration
        if (!existingEntity.getAccountName().equals(dto.getAccountName()) && 
            repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("Kubernetes configuration already exists with account name: " + dto.getAccountName());
        }
        
        // Update entity fields
        existingEntity.setAccountName(dto.getAccountName());
        existingEntity.setClusterName(dto.getClusterName());
        existingEntity.setKubeconfig(dto.getKubeconfig());
        existingEntity.setNamespace(dto.getNamespace());
        existingEntity.setRegion(dto.getRegion());
        existingEntity.setDescription(dto.getDescription());
        
        KubernetesConfig savedEntity = repository.save(existingEntity);
        
        log.info("Successfully updated Kubernetes configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Delete a Kubernetes configuration
     * 
     * @param id the configuration ID
     * @throws ResourceNotFoundException if configuration not found
     */
    public void deleteConfiguration(String id) {
        log.info("Deleting Kubernetes configuration with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Kubernetes configuration not found with ID: " + id);
        }
        
        repository.deleteById(id);
        log.info("Successfully deleted Kubernetes configuration with ID: {}", id);
    }

    /**
     * Search Kubernetes configurations across multiple fields
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return Page of matching Kubernetes configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<KubernetesConfigDTO> searchConfigurations(String searchTerm, Pageable pageable) {
        log.debug("Searching Kubernetes configurations with term: {}", searchTerm);
        
        Page<KubernetesConfig> entities = repository.searchConfigurations(searchTerm, pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Check if a Kubernetes configuration exists with the given account name
     * 
     * @param accountName the Kubernetes account name
     * @return true if configuration exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByAccountName(String accountName) {
        return repository.existsByAccountName(accountName);
    }

    /**
     * Test connection to Kubernetes using the provided configuration
     * 
     * @param dto the Kubernetes configuration DTO to test
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection(KubernetesConfigDTO dto) {
        log.debug("Testing Kubernetes connection for account: {}", dto.getAccountName());
        
        try {
            // TODO: Implement actual Kubernetes API connection test
            // For now, return true if required fields are present
            boolean isValid = dto.getAccountName() != null && !dto.getAccountName().trim().isEmpty() &&
                             dto.getClusterName() != null && !dto.getClusterName().trim().isEmpty() &&
                             dto.getKubeconfig() != null && !dto.getKubeconfig().trim().isEmpty();
            
            log.info("Kubernetes connection test for account '{}': {}", 
                    dto.getAccountName(), isValid ? "SUCCESS" : "FAILED");
            return isValid;
            
        } catch (Exception e) {
            log.error("Kubernetes connection test failed for account '{}': {}", 
                     dto.getAccountName(), e.getMessage());
            return false;
        }
    }

    /**
     * Convert Kubernetes entity to DTO
     * 
     * @param entity the Kubernetes configuration entity
     * @return Kubernetes configuration DTO
     */
    private KubernetesConfigDTO convertToDTO(KubernetesConfig entity) {
        KubernetesConfigDTO dto = new KubernetesConfigDTO();
        dto.setId(entity.getId());
        dto.setAccountName(entity.getAccountName());
        dto.setClusterName(entity.getClusterName());
        dto.setKubeconfig(entity.getKubeconfig());
        dto.setNamespace(entity.getNamespace());
        dto.setRegion(entity.getRegion());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * Convert Kubernetes DTO to entity
     * 
     * @param dto the Kubernetes configuration DTO
     * @return Kubernetes configuration entity
     */
    private KubernetesConfig convertToEntity(KubernetesConfigDTO dto) {
        KubernetesConfig entity = new KubernetesConfig();
        entity.setAccountName(dto.getAccountName());
        entity.setClusterName(dto.getClusterName());
        entity.setKubeconfig(dto.getKubeconfig());
        entity.setNamespace(dto.getNamespace());
        entity.setRegion(dto.getRegion());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
