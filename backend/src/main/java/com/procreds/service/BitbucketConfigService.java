package com.procreds.service;

import com.procreds.dto.BitbucketConfigDTO;
import com.procreds.entity.BitbucketConfig;
import com.procreds.exception.ResourceAlreadyExistsException;
import com.procreds.exception.ResourceNotFoundException;
import com.procreds.repository.BitbucketConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for Bitbucket configuration management
 * 
 * Provides business logic for CRUD operations on Bitbucket configurations.
 * Handles validation, error handling, and data transformation between
 * DTOs and entities.
 * 
 * @author ProCreds Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BitbucketConfigService {

    private final BitbucketConfigRepository repository;

    /**
     * Retrieve all Bitbucket configurations with pagination
     * 
     * @param pageable pagination information
     * @return Page of Bitbucket configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<BitbucketConfigDTO> getAllConfigurations(Pageable pageable) {
        log.debug("Retrieving all Bitbucket configurations with pagination: {}", pageable);
        
        Page<BitbucketConfig> entities = repository.findAllByOrderByUsernameAsc(pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Retrieve Bitbucket configuration by ID
     * 
     * @param id the configuration ID
     * @return Bitbucket configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public BitbucketConfigDTO getConfigurationById(String id) {
        log.debug("Retrieving Bitbucket configuration by ID: {}", id);
        
        BitbucketConfig entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bitbucket configuration not found with ID: " + id));
        
        return convertToDTO(entity);
    }

    /**
     * Retrieve Bitbucket configuration by username
     * 
     * @param username the Bitbucket username
     * @return Bitbucket configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public BitbucketConfigDTO getConfigurationByUsername(String username) {
        log.debug("Retrieving Bitbucket configuration by username: {}", username);
        
        BitbucketConfig entity = repository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Bitbucket configuration not found with username: " + username));
        
        return convertToDTO(entity);
    }

    /**
     * Create a new Bitbucket configuration
     * 
     * @param dto the Bitbucket configuration DTO
     * @return created Bitbucket configuration DTO
     * @throws ResourceAlreadyExistsException if username already exists
     */
    public BitbucketConfigDTO createConfiguration(BitbucketConfigDTO dto) {
        log.info("Creating new Bitbucket configuration for username: {}", dto.getUsername());
        
        // Check if username already exists
        if (repository.existsByUsername(dto.getUsername())) {
            throw new ResourceAlreadyExistsException("Bitbucket configuration already exists with username: " + dto.getUsername());
        }
        
        BitbucketConfig entity = convertToEntity(dto);
        BitbucketConfig savedEntity = repository.save(entity);
        
        log.info("Successfully created Bitbucket configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Update an existing Bitbucket configuration
     * 
     * @param id the configuration ID
     * @param dto the updated Bitbucket configuration DTO
     * @return updated Bitbucket configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     * @throws ResourceAlreadyExistsException if username conflicts with existing configuration
     */
    public BitbucketConfigDTO updateConfiguration(String id, BitbucketConfigDTO dto) {
        log.info("Updating Bitbucket configuration with ID: {}", id);
        
        BitbucketConfig existingEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Bitbucket configuration not found with ID: " + id));
        
        // Check if username conflicts with another configuration
        if (!existingEntity.getUsername().equals(dto.getUsername()) && 
            repository.existsByUsername(dto.getUsername())) {
            throw new ResourceAlreadyExistsException("Bitbucket configuration already exists with username: " + dto.getUsername());
        }
        
        // Update entity fields
        existingEntity.setUsername(dto.getUsername());
        existingEntity.setAppPassword(dto.getAppPassword());
        existingEntity.setWorkspace(dto.getWorkspace());
        existingEntity.setApiUrl(dto.getApiUrl());
        existingEntity.setDescription(dto.getDescription());
        
        BitbucketConfig savedEntity = repository.save(existingEntity);
        
        log.info("Successfully updated Bitbucket configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Delete a Bitbucket configuration
     * 
     * @param id the configuration ID
     * @throws ResourceNotFoundException if configuration not found
     */
    public void deleteConfiguration(String id) {
        log.info("Deleting Bitbucket configuration with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Bitbucket configuration not found with ID: " + id);
        }
        
        repository.deleteById(id);
        log.info("Successfully deleted Bitbucket configuration with ID: {}", id);
    }

    /**
     * Search Bitbucket configurations across multiple fields
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return Page of matching Bitbucket configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<BitbucketConfigDTO> searchConfigurations(String searchTerm, Pageable pageable) {
        log.debug("Searching Bitbucket configurations with term: {}", searchTerm);
        
        Page<BitbucketConfig> entities = repository.searchConfigurations(searchTerm, pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Check if a Bitbucket configuration exists with the given username
     * 
     * @param username the Bitbucket username
     * @return true if configuration exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    /**
     * Test connection to Bitbucket using the provided configuration
     * 
     * @param dto the Bitbucket configuration DTO to test
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection(BitbucketConfigDTO dto) {
        log.debug("Testing Bitbucket connection for username: {}", dto.getUsername());
        
        try {
            // TODO: Implement actual Bitbucket API connection test
            // For now, return true if required fields are present
            boolean isValid = dto.getUsername() != null && !dto.getUsername().trim().isEmpty() &&
                             dto.getAppPassword() != null && !dto.getAppPassword().trim().isEmpty();
            
            log.info("Bitbucket connection test for username '{}': {}", 
                    dto.getUsername(), isValid ? "SUCCESS" : "FAILED");
            return isValid;
            
        } catch (Exception e) {
            log.error("Bitbucket connection test failed for username '{}': {}", 
                     dto.getUsername(), e.getMessage());
            return false;
        }
    }

    /**
     * Convert Bitbucket entity to DTO
     * 
     * @param entity the Bitbucket configuration entity
     * @return Bitbucket configuration DTO
     */
    private BitbucketConfigDTO convertToDTO(BitbucketConfig entity) {
        BitbucketConfigDTO dto = new BitbucketConfigDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setAppPassword(entity.getAppPassword());
        dto.setWorkspace(entity.getWorkspace());
        dto.setApiUrl(entity.getApiUrl());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * Convert Bitbucket DTO to entity
     * 
     * @param dto the Bitbucket configuration DTO
     * @return Bitbucket configuration entity
     */
    private BitbucketConfig convertToEntity(BitbucketConfigDTO dto) {
        BitbucketConfig entity = new BitbucketConfig();
        entity.setUsername(dto.getUsername());
        entity.setAppPassword(dto.getAppPassword());
        entity.setWorkspace(dto.getWorkspace());
        entity.setApiUrl(dto.getApiUrl());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
