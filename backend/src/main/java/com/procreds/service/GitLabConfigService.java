package com.procreds.service;

import com.procreds.dto.GitLabConfigDTO;
import com.procreds.entity.GitLabConfig;
import com.procreds.exception.ResourceAlreadyExistsException;
import com.procreds.exception.ResourceNotFoundException;
import com.procreds.repository.GitLabConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for GitLab configuration management
 * 
 * Provides business logic for CRUD operations on GitLab configurations.
 * Handles validation, error handling, and data transformation between
 * DTOs and entities.
 * 
 * @author ProCreds Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GitLabConfigService {

    private final GitLabConfigRepository repository;

    /**
     * Retrieve all GitLab configurations with pagination
     * 
     * @param pageable pagination information
     * @return Page of GitLab configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<GitLabConfigDTO> getAllConfigurations(Pageable pageable) {
        log.debug("Retrieving all GitLab configurations with pagination: {}", pageable);
        
        Page<GitLabConfig> entities = repository.findAllByOrderByAccountNameAsc(pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Retrieve GitLab configuration by ID
     * 
     * @param id the configuration ID
     * @return GitLab configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public GitLabConfigDTO getConfigurationById(String id) {
        log.debug("Retrieving GitLab configuration by ID: {}", id);
        
        GitLabConfig entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("GitLab configuration not found with ID: " + id));
        
        return convertToDTO(entity);
    }

    /**
     * Retrieve GitLab configuration by account name
     * 
     * @param accountName the GitLab account name
     * @return GitLab configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public GitLabConfigDTO getConfigurationByAccountName(String accountName) {
        log.debug("Retrieving GitLab configuration by account name: {}", accountName);
        
        GitLabConfig entity = repository.findByAccountName(accountName)
            .orElseThrow(() -> new ResourceNotFoundException("GitLab configuration not found with account name: " + accountName));
        
        return convertToDTO(entity);
    }

    /**
     * Create a new GitLab configuration
     * 
     * @param dto the GitLab configuration DTO
     * @return created GitLab configuration DTO
     * @throws ResourceAlreadyExistsException if account name already exists
     */
    public GitLabConfigDTO createConfiguration(GitLabConfigDTO dto) {
        log.info("Creating new GitLab configuration for account: {}", dto.getAccountName());
        
        // Check if account name already exists
        if (repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("GitLab configuration already exists with account name: " + dto.getAccountName());
        }
        
        GitLabConfig entity = convertToEntity(dto);
        GitLabConfig savedEntity = repository.save(entity);
        
        log.info("Successfully created GitLab configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Update an existing GitLab configuration
     * 
     * @param id the configuration ID
     * @param dto the updated GitLab configuration DTO
     * @return updated GitLab configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     * @throws ResourceAlreadyExistsException if account name conflicts with existing configuration
     */
    public GitLabConfigDTO updateConfiguration(String id, GitLabConfigDTO dto) {
        log.info("Updating GitLab configuration with ID: {}", id);
        
        GitLabConfig existingEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("GitLab configuration not found with ID: " + id));
        
        // Check if account name conflicts with another configuration
        if (!existingEntity.getAccountName().equals(dto.getAccountName()) && 
            repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("GitLab configuration already exists with account name: " + dto.getAccountName());
        }
        
        // Update entity fields
        existingEntity.setAccountName(dto.getAccountName());
        existingEntity.setPersonalAccessToken(dto.getPersonalAccessToken());
        existingEntity.setGroupId(dto.getGroupId());
        existingEntity.setApiUrl(dto.getApiUrl());
        existingEntity.setDescription(dto.getDescription());
        
        GitLabConfig savedEntity = repository.save(existingEntity);
        
        log.info("Successfully updated GitLab configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Delete a GitLab configuration
     * 
     * @param id the configuration ID
     * @throws ResourceNotFoundException if configuration not found
     */
    public void deleteConfiguration(String id) {
        log.info("Deleting GitLab configuration with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("GitLab configuration not found with ID: " + id);
        }
        
        repository.deleteById(id);
        log.info("Successfully deleted GitLab configuration with ID: {}", id);
    }

    /**
     * Search GitLab configurations across multiple fields
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return Page of matching GitLab configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<GitLabConfigDTO> searchConfigurations(String searchTerm, Pageable pageable) {
        log.debug("Searching GitLab configurations with term: {}", searchTerm);
        
        Page<GitLabConfig> entities = repository.searchConfigurations(searchTerm, pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Check if a GitLab configuration exists with the given account name
     * 
     * @param accountName the GitLab account name
     * @return true if configuration exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByAccountName(String accountName) {
        return repository.existsByAccountName(accountName);
    }

    /**
     * Test connection to GitLab using the provided configuration
     * 
     * @param dto the GitLab configuration DTO to test
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection(GitLabConfigDTO dto) {
        log.debug("Testing GitLab connection for account: {}", dto.getAccountName());
        
        try {
            // TODO: Implement actual GitLab API connection test
            // For now, return true if required fields are present
            boolean isValid = dto.getAccountName() != null && !dto.getAccountName().trim().isEmpty() &&
                             dto.getPersonalAccessToken() != null && !dto.getPersonalAccessToken().trim().isEmpty();
            
            log.info("GitLab connection test for account '{}': {}", 
                    dto.getAccountName(), isValid ? "SUCCESS" : "FAILED");
            return isValid;
            
        } catch (Exception e) {
            log.error("GitLab connection test failed for account '{}': {}", 
                     dto.getAccountName(), e.getMessage());
            return false;
        }
    }

    /**
     * Convert GitLab entity to DTO
     * 
     * @param entity the GitLab configuration entity
     * @return GitLab configuration DTO
     */
    private GitLabConfigDTO convertToDTO(GitLabConfig entity) {
        GitLabConfigDTO dto = new GitLabConfigDTO();
        dto.setId(entity.getId());
        dto.setAccountName(entity.getAccountName());
        dto.setPersonalAccessToken(entity.getPersonalAccessToken());
        dto.setGroupId(entity.getGroupId());
        dto.setApiUrl(entity.getApiUrl());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * Convert GitLab DTO to entity
     * 
     * @param dto the GitLab configuration DTO
     * @return GitLab configuration entity
     */
    private GitLabConfig convertToEntity(GitLabConfigDTO dto) {
        GitLabConfig entity = new GitLabConfig();
        entity.setAccountName(dto.getAccountName());
        entity.setPersonalAccessToken(dto.getPersonalAccessToken());
        entity.setGroupId(dto.getGroupId());
        entity.setApiUrl(dto.getApiUrl());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
