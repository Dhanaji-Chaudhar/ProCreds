package com.procreds.service;

import com.procreds.dto.JenkinsConfigDTO;
import com.procreds.entity.JenkinsConfig;
import com.procreds.exception.ResourceAlreadyExistsException;
import com.procreds.exception.ResourceNotFoundException;
import com.procreds.repository.JenkinsConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for Jenkins configuration management
 * 
 * Provides business logic for CRUD operations on Jenkins configurations.
 * Handles validation, error handling, and data transformation between
 * DTOs and entities.
 * 
 * @author ProCreds Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JenkinsConfigService {

    private final JenkinsConfigRepository repository;

    /**
     * Retrieve all Jenkins configurations with pagination
     * 
     * @param pageable pagination information
     * @return Page of Jenkins configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<JenkinsConfigDTO> getAllConfigurations(Pageable pageable) {
        log.debug("Retrieving all Jenkins configurations with pagination: {}", pageable);
        
        Page<JenkinsConfig> entities = repository.findAllByOrderByAccountNameAsc(pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Retrieve Jenkins configuration by ID
     * 
     * @param id the configuration ID
     * @return Jenkins configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public JenkinsConfigDTO getConfigurationById(String id) {
        log.debug("Retrieving Jenkins configuration by ID: {}", id);
        
        JenkinsConfig entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Jenkins configuration not found with ID: " + id));
        
        return convertToDTO(entity);
    }

    /**
     * Retrieve Jenkins configuration by account name
     * 
     * @param accountName the Jenkins account name
     * @return Jenkins configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public JenkinsConfigDTO getConfigurationByAccountName(String accountName) {
        log.debug("Retrieving Jenkins configuration by account name: {}", accountName);
        
        JenkinsConfig entity = repository.findByAccountName(accountName)
            .orElseThrow(() -> new ResourceNotFoundException("Jenkins configuration not found with account name: " + accountName));
        
        return convertToDTO(entity);
    }

    /**
     * Create a new Jenkins configuration
     * 
     * @param dto the Jenkins configuration DTO
     * @return created Jenkins configuration DTO
     * @throws ResourceAlreadyExistsException if account name already exists
     */
    public JenkinsConfigDTO createConfiguration(JenkinsConfigDTO dto) {
        log.info("Creating new Jenkins configuration for account: {}", dto.getAccountName());
        
        // Check if account name already exists
        if (repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("Jenkins configuration already exists with account name: " + dto.getAccountName());
        }
        
        JenkinsConfig entity = convertToEntity(dto);
        JenkinsConfig savedEntity = repository.save(entity);
        
        log.info("Successfully created Jenkins configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Update an existing Jenkins configuration
     * 
     * @param id the configuration ID
     * @param dto the updated Jenkins configuration DTO
     * @return updated Jenkins configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     * @throws ResourceAlreadyExistsException if account name conflicts with existing configuration
     */
    public JenkinsConfigDTO updateConfiguration(String id, JenkinsConfigDTO dto) {
        log.info("Updating Jenkins configuration with ID: {}", id);
        
        JenkinsConfig existingEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Jenkins configuration not found with ID: " + id));
        
        // Check if account name conflicts with another configuration
        if (!existingEntity.getAccountName().equals(dto.getAccountName()) && 
            repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("Jenkins configuration already exists with account name: " + dto.getAccountName());
        }
        
        // Update entity fields
        existingEntity.setAccountName(dto.getAccountName());
        existingEntity.setBaseUrl(dto.getBaseUrl());
        existingEntity.setUsername(dto.getUsername());
        existingEntity.setApiToken(dto.getApiToken());
        existingEntity.setJobPrefix(dto.getJobPrefix());
        existingEntity.setDescription(dto.getDescription());
        
        JenkinsConfig savedEntity = repository.save(existingEntity);
        
        log.info("Successfully updated Jenkins configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Delete a Jenkins configuration
     * 
     * @param id the configuration ID
     * @throws ResourceNotFoundException if configuration not found
     */
    public void deleteConfiguration(String id) {
        log.info("Deleting Jenkins configuration with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Jenkins configuration not found with ID: " + id);
        }
        
        repository.deleteById(id);
        log.info("Successfully deleted Jenkins configuration with ID: {}", id);
    }

    /**
     * Search Jenkins configurations across multiple fields
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return Page of matching Jenkins configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<JenkinsConfigDTO> searchConfigurations(String searchTerm, Pageable pageable) {
        log.debug("Searching Jenkins configurations with term: {}", searchTerm);
        
        Page<JenkinsConfig> entities = repository.searchConfigurations(searchTerm, pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Check if a Jenkins configuration exists with the given account name
     * 
     * @param accountName the Jenkins account name
     * @return true if configuration exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByAccountName(String accountName) {
        return repository.existsByAccountName(accountName);
    }

    /**
     * Test connection to Jenkins using the provided configuration
     * 
     * @param dto the Jenkins configuration DTO to test
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection(JenkinsConfigDTO dto) {
        log.debug("Testing Jenkins connection for account: {}", dto.getAccountName());
        
        try {
            // TODO: Implement actual Jenkins API connection test
            // For now, return true if required fields are present
            boolean isValid = dto.getAccountName() != null && !dto.getAccountName().trim().isEmpty() &&
                             dto.getBaseUrl() != null && !dto.getBaseUrl().trim().isEmpty() &&
                             dto.getUsername() != null && !dto.getUsername().trim().isEmpty() &&
                             dto.getApiToken() != null && !dto.getApiToken().trim().isEmpty();
            
            log.info("Jenkins connection test for account '{}': {}", 
                    dto.getAccountName(), isValid ? "SUCCESS" : "FAILED");
            return isValid;
            
        } catch (Exception e) {
            log.error("Jenkins connection test failed for account '{}': {}", 
                     dto.getAccountName(), e.getMessage());
            return false;
        }
    }

    /**
     * Convert Jenkins entity to DTO
     * 
     * @param entity the Jenkins configuration entity
     * @return Jenkins configuration DTO
     */
    private JenkinsConfigDTO convertToDTO(JenkinsConfig entity) {
        JenkinsConfigDTO dto = new JenkinsConfigDTO();
        dto.setId(entity.getId());
        dto.setAccountName(entity.getAccountName());
        dto.setBaseUrl(entity.getBaseUrl());
        dto.setUsername(entity.getUsername());
        dto.setApiToken(entity.getApiToken());
        dto.setJobPrefix(entity.getJobPrefix());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * Convert Jenkins DTO to entity
     * 
     * @param dto the Jenkins configuration DTO
     * @return Jenkins configuration entity
     */
    private JenkinsConfig convertToEntity(JenkinsConfigDTO dto) {
        JenkinsConfig entity = new JenkinsConfig();
        entity.setAccountName(dto.getAccountName());
        entity.setBaseUrl(dto.getBaseUrl());
        entity.setUsername(dto.getUsername());
        entity.setApiToken(dto.getApiToken());
        entity.setJobPrefix(dto.getJobPrefix());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
