package com.procreds.repository;

import com.procreds.entity.JenkinsConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JenkinsConfigRepository extends MongoRepository<JenkinsConfig, String> {

    /**
     * Find configuration by account name
     */
    Optional<JenkinsConfig> findByAccountName(String accountName);

    /**
     * Check if configuration exists by account name
     */
    boolean existsByAccountName(String accountName);

    /**
     * Find configurations by base URL
     */
    Page<JenkinsConfig> findByBaseUrlContainingIgnoreCase(String baseUrl, Pageable pageable);

    /**
     * Find configurations by username
     */
    Page<JenkinsConfig> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    /**
     * Search configurations by account name, base URL, username, or description
     */
    @Query("{ $or: [ " +
           "{ 'accountName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'baseUrl': { $regex: ?0, $options: 'i' } }, " +
           "{ 'username': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<JenkinsConfig> searchConfigurations(String searchTerm, Pageable pageable);

    /**
     * Find all configurations ordered by account name
     */
    Page<JenkinsConfig> findAllByOrderByAccountNameAsc(Pageable pageable);

    /**
     * Delete configuration by account name
     */
    void deleteByAccountName(String accountName);
}

