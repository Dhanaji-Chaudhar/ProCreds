package com.procreds.service;

import com.procreds.dto.GitHubConfigDTO;
import com.procreds.entity.GitHubConfig;
import com.procreds.exception.ResourceAlreadyExistsException;
import com.procreds.exception.ResourceNotFoundException;
import com.procreds.repository.GitHubConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for GitHub configuration management
 * 
 * Provides business logic for CRUD operations on GitHub configurations.
 * Handles validation, error handling, and data transformation between
 * DTOs and entities.
 * 
 * @author ProCreds Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GitHubConfigService {

    private final GitHubConfigRepository repository;

    /**
     * Retrieve all GitHub configurations with pagination
     * 
     * @param pageable pagination information
     * @return Page of GitHub configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<GitHubConfigDTO> getAllConfigurations(Pageable pageable) {
        log.debug("Retrieving all GitHub configurations with pagination: {}", pageable);
        
        Page<GitHubConfig> entities = repository.findAll(pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Retrieve GitHub configuration by ID
     * 
     * @param id the configuration ID
     * @return GitHub configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public GitHubConfigDTO getConfigurationById(String id) {
        log.debug("Retrieving GitHub configuration by ID: {}", id);
        
        GitHubConfig entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("GitHub configuration not found with ID: " + id));
        
        return convertToDTO(entity);
    }

    /**
     * Retrieve GitHub configuration by account name
     * 
     * @param accountName the GitHub account name
     * @return GitHub configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public GitHubConfigDTO getConfigurationByAccountName(String accountName) {
        log.debug("Retrieving GitHub configuration by account name: {}", accountName);
        
        GitHubConfig entity = repository.findByAccountName(accountName)
            .orElseThrow(() -> new ResourceNotFoundException("GitHub configuration not found with account name: " + accountName));
        
        return convertToDTO(entity);
    }

    /**
     * Create a new GitHub configuration
     * 
     * @param dto the GitHub configuration DTO
     * @return created GitHub configuration DTO
     * @throws ResourceAlreadyExistsException if account name already exists
     */
    public GitHubConfigDTO createConfiguration(GitHubConfigDTO dto) {
        log.info("Creating new GitHub configuration for account: {}", dto.getAccountName());
        
        // Check if account name already exists
        if (repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("GitHub configuration already exists with account name: " + dto.getAccountName());
        }
        
        GitHubConfig entity = convertToEntity(dto);
        GitHubConfig savedEntity = repository.save(entity);
        
        log.info("Successfully created GitHub configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Update an existing GitHub configuration
     * 
     * @param id the configuration ID
     * @param dto the updated GitHub configuration DTO
     * @return updated GitHub configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     * @throws ResourceAlreadyExistsException if account name conflicts with existing configuration
     */
    public GitHubConfigDTO updateConfiguration(String id, GitHubConfigDTO dto) {
        log.info("Updating GitHub configuration with ID: {}", id);
        
        GitHubConfig existingEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("GitHub configuration not found with ID: " + id));
        
        // Check if account name conflicts with another configuration
        if (!existingEntity.getAccountName().equals(dto.getAccountName()) && 
            repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("GitHub configuration already exists with account name: " + dto.getAccountName());
        }
        
        // Update entity fields
        existingEntity.setAccountName(dto.getAccountName());
        existingEntity.setAccessToken(dto.getAccessToken());
        existingEntity.setOrganization(dto.getOrganization());
        existingEntity.setApiUrl(dto.getApiUrl());
        existingEntity.setDescription(dto.getDescription());
        
        GitHubConfig savedEntity = repository.save(existingEntity);
        
        log.info("Successfully updated GitHub configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Delete a GitHub configuration
     * 
     * @param id the configuration ID
     * @throws ResourceNotFoundException if configuration not found
     */
    public void deleteConfiguration(String id) {
        log.info("Deleting GitHub configuration with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("GitHub configuration not found with ID: " + id);
        }
        
        repository.deleteById(id);
        log.info("Successfully deleted GitHub configuration with ID: {}", id);
    }

    /**
     * Search GitHub configurations across multiple fields
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return Page of matching GitHub configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<GitHubConfigDTO> searchConfigurations(String searchTerm, Pageable pageable) {
        log.debug("Searching GitHub configurations with term: {}", searchTerm);
        
        Page<GitHubConfig> entities = repository.searchByMultipleFields(searchTerm, pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Check if a GitHub configuration exists with the given account name
     * 
     * @param accountName the GitHub account name
     * @return true if configuration exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByAccountName(String accountName) {
        return repository.existsByAccountName(accountName);
    }

    /**
     * Convert GitHub entity to DTO
     * 
     * @param entity the GitHub configuration entity
     * @return GitHub configuration DTO
     */
    private GitHubConfigDTO convertToDTO(GitHubConfig entity) {
        GitHubConfigDTO dto = new GitHubConfigDTO();
        dto.setId(entity.getId());
        dto.setAccountName(entity.getAccountName());
        dto.setAccessToken(entity.getAccessToken());
        dto.setOrganization(entity.getOrganization());
        dto.setApiUrl(entity.getApiUrl());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * Convert GitHub DTO to entity
     * 
     * @param dto the GitHub configuration DTO
     * @return GitHub configuration entity
     */
    private GitHubConfig convertToEntity(GitHubConfigDTO dto) {
        GitHubConfig entity = new GitHubConfig();
        entity.setAccountName(dto.getAccountName());
        entity.setAccessToken(dto.getAccessToken());
        entity.setOrganization(dto.getOrganization());
        entity.setApiUrl(dto.getApiUrl());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}

