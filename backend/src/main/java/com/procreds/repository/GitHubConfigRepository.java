package com.procreds.repository;

import com.procreds.entity.GitHubConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for GitHub configuration management
 * 
 * Provides CRUD operations and custom queries for GitHub configurations.
 * Extends MongoRepository for standard database operations with additional
 * custom methods for business-specific queries.
 * 
 * @author ProCreds Team
 */
@Repository
public interface GitHubConfigRepository extends MongoRepository<GitHubConfig, String> {

    /**
     * Find GitHub configuration by account name
     * 
     * @param accountName the GitHub account name
     * @return Optional containing the configuration if found
     */
    Optional<GitHubConfig> findByAccountName(String accountName);

    /**
     * Check if a configuration exists with the given account name
     * 
     * @param accountName the GitHub account name
     * @return true if configuration exists, false otherwise
     */
    boolean existsByAccountName(String accountName);

    /**
     * Find all configurations for a specific organization
     * 
     * @param organization the GitHub organization name
     * @param pageable pagination information
     * @return Page of configurations for the organization
     */
    Page<GitHubConfig> findByOrganization(String organization, Pageable pageable);

    /**
     * Find configurations by account name containing the search term (case-insensitive)
     * 
     * @param accountName partial account name to search for
     * @param pageable pagination information
     * @return Page of matching configurations
     */
    Page<GitHubConfig> findByAccountNameContainingIgnoreCase(String accountName, Pageable pageable);

    /**
     * Find configurations by organization containing the search term (case-insensitive)
     * 
     * @param organization partial organization name to search for
     * @param pageable pagination information
     * @return Page of matching configurations
     */
    Page<GitHubConfig> findByOrganizationContainingIgnoreCase(String organization, Pageable pageable);

    /**
     * Find configurations by description containing the search term (case-insensitive)
     * 
     * @param description partial description to search for
     * @param pageable pagination information
     * @return Page of matching configurations
     */
    Page<GitHubConfig> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    /**
     * Search configurations across multiple fields using MongoDB query
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return Page of matching configurations
     */
    @Query("{ $or: [ " +
           "{ 'account_name': { $regex: ?0, $options: 'i' } }, " +
           "{ 'organization': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<GitHubConfig> searchByMultipleFields(String searchTerm, Pageable pageable);

    /**
     * Count configurations by organization
     * 
     * @param organization the GitHub organization name
     * @return number of configurations for the organization
     */
    long countByOrganization(String organization);
}

