package com.procreds.repository;

import com.procreds.entity.JiraConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JiraConfigRepository extends MongoRepository<JiraConfig, String> {

    /**
     * Find configuration by account name
     */
    Optional<JiraConfig> findByAccountName(String accountName);

    /**
     * Check if configuration exists by account name
     */
    boolean existsByAccountName(String accountName);

    /**
     * Find configurations by base URL
     */
    Page<JiraConfig> findByBaseUrlContainingIgnoreCase(String baseUrl, Pageable pageable);

    /**
     * Find configurations by email
     */
    Page<JiraConfig> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    /**
     * Find configurations by project key
     */
    Page<JiraConfig> findByProjectKey(String projectKey, Pageable pageable);

    /**
     * Search configurations by account name, base URL, email, project key, or description
     */
    @Query("{ $or: [ " +
           "{ 'accountName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'baseUrl': { $regex: ?0, $options: 'i' } }, " +
           "{ 'email': { $regex: ?0, $options: 'i' } }, " +
           "{ 'projectKey': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<JiraConfig> searchConfigurations(String searchTerm, Pageable pageable);

    /**
     * Find all configurations ordered by account name
     */
    Page<JiraConfig> findAllByOrderByAccountNameAsc(Pageable pageable);

    /**
     * Delete configuration by account name
     */
    void deleteByAccountName(String accountName);
}

