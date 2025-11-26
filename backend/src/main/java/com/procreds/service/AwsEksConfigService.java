package com.procreds.service;

import com.procreds.dto.AwsEksConfigDTO;
import com.procreds.entity.AwsEksConfig;
import com.procreds.exception.ResourceAlreadyExistsException;
import com.procreds.exception.ResourceNotFoundException;
import com.procreds.repository.AwsEksConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for AWS EKS configuration management
 * 
 * Provides business logic for CRUD operations on AWS EKS configurations.
 * Handles validation, error handling, and data transformation between
 * DTOs and entities.
 * 
 * @author ProCreds Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AwsEksConfigService {

    private final AwsEksConfigRepository repository;

    /**
     * Retrieve all AWS EKS configurations with pagination
     * 
     * @param pageable pagination information
     * @return Page of AWS EKS configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<AwsEksConfigDTO> getAllConfigurations(Pageable pageable) {
        log.debug("Retrieving all AWS EKS configurations with pagination: {}", pageable);
        
        Page<AwsEksConfig> entities = repository.findAllByOrderByAccountNameAsc(pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Retrieve AWS EKS configuration by ID
     * 
     * @param id the configuration ID
     * @return AWS EKS configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public AwsEksConfigDTO getConfigurationById(String id) {
        log.debug("Retrieving AWS EKS configuration by ID: {}", id);
        
        AwsEksConfig entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("AWS EKS configuration not found with ID: " + id));
        
        return convertToDTO(entity);
    }

    /**
     * Retrieve AWS EKS configuration by account name
     * 
     * @param accountName the AWS EKS account name
     * @return AWS EKS configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     */
    @Transactional(readOnly = true)
    public AwsEksConfigDTO getConfigurationByAccountName(String accountName) {
        log.debug("Retrieving AWS EKS configuration by account name: {}", accountName);
        
        AwsEksConfig entity = repository.findByAccountName(accountName)
            .orElseThrow(() -> new ResourceNotFoundException("AWS EKS configuration not found with account name: " + accountName));
        
        return convertToDTO(entity);
    }

    /**
     * Create a new AWS EKS configuration
     * 
     * @param dto the AWS EKS configuration DTO
     * @return created AWS EKS configuration DTO
     * @throws ResourceAlreadyExistsException if account name already exists
     */
    public AwsEksConfigDTO createConfiguration(AwsEksConfigDTO dto) {
        log.info("Creating new AWS EKS configuration for account: {}", dto.getAccountName());
        
        // Check if account name already exists
        if (repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("AWS EKS configuration already exists with account name: " + dto.getAccountName());
        }
        
        AwsEksConfig entity = convertToEntity(dto);
        AwsEksConfig savedEntity = repository.save(entity);
        
        log.info("Successfully created AWS EKS configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Update an existing AWS EKS configuration
     * 
     * @param id the configuration ID
     * @param dto the updated AWS EKS configuration DTO
     * @return updated AWS EKS configuration DTO
     * @throws ResourceNotFoundException if configuration not found
     * @throws ResourceAlreadyExistsException if account name conflicts with existing configuration
     */
    public AwsEksConfigDTO updateConfiguration(String id, AwsEksConfigDTO dto) {
        log.info("Updating AWS EKS configuration with ID: {}", id);
        
        AwsEksConfig existingEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("AWS EKS configuration not found with ID: " + id));
        
        // Check if account name conflicts with another configuration
        if (!existingEntity.getAccountName().equals(dto.getAccountName()) && 
            repository.existsByAccountName(dto.getAccountName())) {
            throw new ResourceAlreadyExistsException("AWS EKS configuration already exists with account name: " + dto.getAccountName());
        }
        
        // Update entity fields
        existingEntity.setAccountName(dto.getAccountName());
        existingEntity.setClusterName(dto.getClusterName());
        existingEntity.setAccessKey(dto.getAccessKey());
        existingEntity.setSecretKey(dto.getSecretKey());
        existingEntity.setRegion(dto.getRegion());
        existingEntity.setDescription(dto.getDescription());
        
        AwsEksConfig savedEntity = repository.save(existingEntity);
        
        log.info("Successfully updated AWS EKS configuration with ID: {}", savedEntity.getId());
        return convertToDTO(savedEntity);
    }

    /**
     * Delete an AWS EKS configuration
     * 
     * @param id the configuration ID
     * @throws ResourceNotFoundException if configuration not found
     */
    public void deleteConfiguration(String id) {
        log.info("Deleting AWS EKS configuration with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("AWS EKS configuration not found with ID: " + id);
        }
        
        repository.deleteById(id);
        log.info("Successfully deleted AWS EKS configuration with ID: {}", id);
    }

    /**
     * Search AWS EKS configurations across multiple fields
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return Page of matching AWS EKS configuration DTOs
     */
    @Transactional(readOnly = true)
    public Page<AwsEksConfigDTO> searchConfigurations(String searchTerm, Pageable pageable) {
        log.debug("Searching AWS EKS configurations with term: {}", searchTerm);
        
        Page<AwsEksConfig> entities = repository.searchConfigurations(searchTerm, pageable);
        return entities.map(this::convertToDTO);
    }

    /**
     * Check if an AWS EKS configuration exists with the given account name
     * 
     * @param accountName the AWS EKS account name
     * @return true if configuration exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByAccountName(String accountName) {
        return repository.existsByAccountName(accountName);
    }

    /**
     * Test connection to AWS EKS using the provided configuration
     * 
     * @param dto the AWS EKS configuration DTO to test
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection(AwsEksConfigDTO dto) {
        log.debug("Testing AWS EKS connection for account: {}", dto.getAccountName());
        
        try {
            // TODO: Implement actual AWS EKS API connection test
            // For now, return true if required fields are present
            boolean isValid = dto.getAccountName() != null && !dto.getAccountName().trim().isEmpty() &&
                             dto.getClusterName() != null && !dto.getClusterName().trim().isEmpty() &&
                             dto.getAccessKey() != null && !dto.getAccessKey().trim().isEmpty() &&
                             dto.getSecretKey() != null && !dto.getSecretKey().trim().isEmpty() &&
                             dto.getRegion() != null && !dto.getRegion().trim().isEmpty();
            
            log.info("AWS EKS connection test for account '{}': {}", 
                    dto.getAccountName(), isValid ? "SUCCESS" : "FAILED");
            return isValid;
            
        } catch (Exception e) {
            log.error("AWS EKS connection test failed for account '{}': {}", 
                     dto.getAccountName(), e.getMessage());
            return false;
        }
    }

    /**
     * Convert AWS EKS entity to DTO
     * 
     * @param entity the AWS EKS configuration entity
     * @return AWS EKS configuration DTO
     */
    private AwsEksConfigDTO convertToDTO(AwsEksConfig entity) {
        AwsEksConfigDTO dto = new AwsEksConfigDTO();
        dto.setId(entity.getId());
        dto.setAccountName(entity.getAccountName());
        dto.setClusterName(entity.getClusterName());
        dto.setAccessKey(entity.getAccessKey());
        dto.setSecretKey(entity.getSecretKey());
        dto.setRegion(entity.getRegion());
        dto.setDescription(entity.getDescription());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * Convert AWS EKS DTO to entity
     * 
     * @param dto the AWS EKS configuration DTO
     * @return AWS EKS configuration entity
     */
    private AwsEksConfig convertToEntity(AwsEksConfigDTO dto) {
        AwsEksConfig entity = new AwsEksConfig();
        entity.setAccountName(dto.getAccountName());
        entity.setClusterName(dto.getClusterName());
        entity.setAccessKey(dto.getAccessKey());
        entity.setSecretKey(dto.getSecretKey());
        entity.setRegion(dto.getRegion());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
