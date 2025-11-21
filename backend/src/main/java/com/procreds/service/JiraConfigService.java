package com.procreds.service;

import com.procreds.dto.JiraConfigDTO;
import com.procreds.entity.JiraConfig;
import com.procreds.exception.ResourceAlreadyExistsException;
import com.procreds.exception.ResourceNotFoundException;
import com.procreds.repository.JiraConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for Jira configuration management
 * 
 * Provides business logic for CRUD operations on Jira configurations.
 * Handles validation, error handling, and data transformation between
 * DTOs and entities.
 * 
 * @author ProCreds Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JiraConfigService {

    private final JiraConfigRepository repository;

    /**
     * Retrieve all Jira configurations with pagination
     * 
     * @param pageable pagination information
     * @return Page of Jira configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<JiraConfigDTO> getAllConfigurations(Pageable pageable) {
        log.debug("Retrieving all Jira configurations with pagination: {}", pageable);
        
        Page<JiraConfig> entities = repository.findAllByOrderByAccountNameAsc(pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Retrieve Jira configuration by ID
     * 
     * @param id the configuration ID
     * @return Jira configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public JiraConfigDTO getConfigurationById(String id) {
        log.debug("Retrieving Jira configuration by ID: {}", id);
        
        JiraConfig entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Jira configuration not found with ID: " + id));
        
        return convertToDTO(entity);
    }

    /**
     * Retrieve Jira configuration by account name
     * 
     * @param accountName the Jira account name
     * @return Jira configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public JiraConfigDTO getConfigurationByAccountName(String accountName) {
        log.debug("Retrieving Jira configuration by account name: {}", accountName);
        
        JiraConfig entity = repository.findByAccountName(accountName)
            .orElseThrow(() -> new ResourceNotFoundException("Jira configuration not found with account name: " + accountName));
        
        return convertToDTO(entity);
    }

    /**
     * Create a new Jira configuration
     * 
     * @param dto the Jira configuration DTO
     * @return created Jira configuration DTO
     * @throws ResourceAlreadyExistsException if account name already exists
     */
    public JiraConfigDTO createConfiguration(JiraConfigDTO dto) {
        log.info("Creating new Jira configuration for account: {}", dto.getAccountName());
        
        // Check if account name already exists
        if (repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("Jira configuration already exists with account name: " + dto.getAccountName());
        }
        
        JiraConfig entity = convertToEntity(dto);
        JiraConfig savedEntity = repository.save(entity);
        
        log.info("Successfully created Jira configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Update an existing Jira configuration
     * 
     * @param id the configuration ID
     * @param dto the updated Jira configuration DTO
     * @return updated Jira configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     * @throws ResourceAlreadyExistsException if account name conflicts with existing configuration
     */
    public JiraConfigDTO updateConfiguration(String id, JiraConfigDTO dto) {
        log.info("Updating Jira configuration with ID: {}", id);
        
        JiraConfig existingEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Jira configuration not found with ID: " + id));
        
        // Check if account name conflicts with another configuration
        if (!existingEntity.getAccountName().equals(dto.getAccountName()) && 
            repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("Jira configuration already exists with account name: " + dto.getAccountName());
        }
        
        // Update entity fields
        existingEntity.setAccountName(dto.getAccountName());
        existingEntity.setBaseUrl(dto.getBaseUrl());
        existingEntity.setEmail(dto.getEmail());
        existingEntity.setApiToken(dto.getApiToken());
        existingEntity.setProjectKey(dto.getProjectKey());
        existingEntity.setIssueTypeDefault(dto.getIssueTypeDefault());
        existingEntity.setDescription(dto.getDescription());
        
        JiraConfig savedEntity = repository.save(existingEntity);
        
        log.info("Successfully updated Jira configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Delete a Jira configuration
     * 
     * @param id the configuration ID
     * @throws ResourceNotFoundException if configuration not found
     */
    public void deleteConfiguration(String id) {
        log.info("Deleting Jira configuration with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Jira configuration not found with ID: " + id);
        }
        
        repository.deleteById(id);
        log.info("Successfully deleted Jira configuration with ID: {}", id);
    }

    /**
     * Search Jira configurations across multiple fields
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return Page of matching Jira configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<JiraConfigDTO> searchConfigurations(String searchTerm, Pageable pageable) {
        log.debug("Searching Jira configurations with term: {}", searchTerm);
        
        Page<JiraConfig> entities = repository.searchConfigurations(searchTerm, pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Check if a Jira configuration exists with the given account name
     * 
     * @param accountName the Jira account name
     * @return true if configuration exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByAccountName(String accountName) {
        return repository.existsByAccountName(accountName);
    }

    /**
     * Convert Jira entity to DTO
     * 
     * @param entity the Jira configuration entity
     * @return Jira configuration DTO
     */
    private JiraConfigDTO convertToDTO(JiraConfig entity) {
        JiraConfigDTO dto = new JiraConfigDTO();
        dto.setId(entity.getId());
        dto.setAccountName(entity.getAccountName());
        dto.setBaseUrl(entity.getBaseUrl());
        dto.setEmail(entity.getEmail());
        dto.setApiToken(entity.getApiToken());
        dto.setProjectKey(entity.getProjectKey());
        dto.setIssueTypeDefault(entity.getIssueTypeDefault());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * Convert Jira DTO to entity
     * 
     * @param dto the Jira configuration DTO
     * @return Jira configuration entity
     */
    private JiraConfig convertToEntity(JiraConfigDTO dto) {
        JiraConfig entity = new JiraConfig();
        entity.setAccountName(dto.getAccountName());
        entity.setBaseUrl(dto.getBaseUrl());
        entity.setEmail(dto.getEmail());
        entity.setApiToken(dto.getApiToken());
        entity.setProjectKey(dto.getProjectKey());
        entity.setIssueTypeDefault(dto.getIssueTypeDefault());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}

