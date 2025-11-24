package com.procreds.service;

import com.procreds.dto.SonarQubeConfigDTO;
import com.procreds.entity.SonarQubeConfig;
import com.procreds.exception.ResourceAlreadyExistsException;
import com.procreds.exception.ResourceNotFoundException;
import com.procreds.repository.SonarQubeConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for SonarQube configuration management
 * 
 * Provides business logic for CRUD operations on SonarQube configurations.
 * Handles validation, error handling, and data transformation between
 * DTOs and entities.
 * 
 * @author ProCreds Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SonarQubeConfigService {

    private final SonarQubeConfigRepository repository;

    /**
     * Retrieve all SonarQube configurations with pagination
     * 
     * @param pageable pagination information
     * @return Page of SonarQube configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<SonarQubeConfigDTO> getAllConfigurations(Pageable pageable) {
        log.debug("Retrieving all SonarQube configurations with pagination: {}", pageable);
        
        Page<SonarQubeConfig> entities = repository.findAllByOrderByAccountNameAsc(pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Retrieve SonarQube configuration by ID
     * 
     * @param id the configuration ID
     * @return SonarQube configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public SonarQubeConfigDTO getConfigurationById(String id) {
        log.debug("Retrieving SonarQube configuration by ID: {}", id);
        
        SonarQubeConfig entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("SonarQube configuration not found with ID: " + id));
        
        return convertToDTO(entity);
    }

    /**
     * Retrieve SonarQube configuration by account name
     * 
     * @param accountName the SonarQube account name
     * @return SonarQube configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public SonarQubeConfigDTO getConfigurationByAccountName(String accountName) {
        log.debug("Retrieving SonarQube configuration by account name: {}", accountName);
        
        SonarQubeConfig entity = repository.findByAccountName(accountName)
            .orElseThrow(() -> new ResourceNotFoundException("SonarQube configuration not found with account name: " + accountName));
        
        return convertToDTO(entity);
    }

    /**
     * Create a new SonarQube configuration
     * 
     * @param dto the SonarQube configuration DTO
     * @return created SonarQube configuration DTO
     * @throws ResourceAlreadyExistsException if account name already exists
     */
    public SonarQubeConfigDTO createConfiguration(SonarQubeConfigDTO dto) {
        log.info("Creating new SonarQube configuration for account: {}", dto.getAccountName());
        
        // Check if account name already exists
        if (repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("SonarQube configuration already exists with account name: " + dto.getAccountName());
        }
        
        SonarQubeConfig entity = convertToEntity(dto);
        SonarQubeConfig savedEntity = repository.save(entity);
        
        log.info("Successfully created SonarQube configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Update an existing SonarQube configuration
     * 
     * @param id the configuration ID
     * @param dto the updated SonarQube configuration DTO
     * @return updated SonarQube configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     * @throws ResourceAlreadyExistsException if account name conflicts with existing configuration
     */
    public SonarQubeConfigDTO updateConfiguration(String id, SonarQubeConfigDTO dto) {
        log.info("Updating SonarQube configuration with ID: {}", id);
        
        SonarQubeConfig existingEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("SonarQube configuration not found with ID: " + id));
        
        // Check if account name conflicts with another configuration
        if (!existingEntity.getAccountName().equals(dto.getAccountName()) && 
            repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("SonarQube configuration already exists with account name: " + dto.getAccountName());
        }
        
        // Update entity fields
        existingEntity.setAccountName(dto.getAccountName());
        existingEntity.setBaseUrl(dto.getBaseUrl());
        existingEntity.setToken(dto.getToken());
        existingEntity.setOrganization(dto.getOrganization());
        existingEntity.setDescription(dto.getDescription());
        
        SonarQubeConfig savedEntity = repository.save(existingEntity);
        
        log.info("Successfully updated SonarQube configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Delete a SonarQube configuration
     * 
     * @param id the configuration ID
     * @throws ResourceNotFoundException if configuration not found
     */
    public void deleteConfiguration(String id) {
        log.info("Deleting SonarQube configuration with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("SonarQube configuration not found with ID: " + id);
        }
        
        repository.deleteById(id);
        log.info("Successfully deleted SonarQube configuration with ID: {}", id);
    }

    /**
     * Search SonarQube configurations across multiple fields
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return Page of matching SonarQube configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<SonarQubeConfigDTO> searchConfigurations(String searchTerm, Pageable pageable) {
        log.debug("Searching SonarQube configurations with term: {}", searchTerm);
        
        Page<SonarQubeConfig> entities = repository.searchConfigurations(searchTerm, pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Check if a SonarQube configuration exists with the given account name
     * 
     * @param accountName the SonarQube account name
     * @return true if configuration exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByAccountName(String accountName) {
        return repository.existsByAccountName(accountName);
    }

    /**
     * Convert SonarQube entity to DTO
     * 
     * @param entity the SonarQube configuration entity
     * @return SonarQube configuration DTO
     */
    private SonarQubeConfigDTO convertToDTO(SonarQubeConfig entity) {
        SonarQubeConfigDTO dto = new SonarQubeConfigDTO();
        dto.setId(entity.getId());
        dto.setAccountName(entity.getAccountName());
        dto.setBaseUrl(entity.getBaseUrl());
        dto.setToken(entity.getToken());
        dto.setOrganization(entity.getOrganization());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * Convert SonarQube DTO to entity
     * 
     * @param dto the SonarQube configuration DTO
     * @return SonarQube configuration entity
     */
    private SonarQubeConfig convertToEntity(SonarQubeConfigDTO dto) {
        SonarQubeConfig entity = new SonarQubeConfig();
        entity.setAccountName(dto.getAccountName());
        entity.setBaseUrl(dto.getBaseUrl());
        entity.setToken(dto.getToken());
        entity.setOrganization(dto.getOrganization());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}

