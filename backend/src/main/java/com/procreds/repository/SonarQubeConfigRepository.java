package com.procreds.repository;

import com.procreds.entity.SonarQubeConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SonarQubeConfigRepository extends MongoRepository<SonarQubeConfig, String> {

    /**
     * Find configuration by account name
     */
    Optional<SonarQubeConfig> findByAccountName(String accountName);

    /**
     * Check if configuration exists by account name
     */
    boolean existsByAccountName(String accountName);

    /**
     * Find configurations by base URL
     */
    Page<SonarQubeConfig> findByBaseUrlContainingIgnoreCase(String baseUrl, Pageable pageable);

    /**
     * Find configurations by organization
     */
    Page<SonarQubeConfig> findByOrganizationContainingIgnoreCase(String organization, Pageable pageable);

    /**
     * Search configurations by account name, base URL, organization, or description
     */
    @Query("{ $or: [ " +
           "{ 'accountName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'baseUrl': { $regex: ?0, $options: 'i' } }, " +
           "{ 'organization': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<SonarQubeConfig> searchConfigurations(String searchTerm, Pageable pageable);

    /**
     * Find all configurations ordered by account name
     */
    Page<SonarQubeConfig> findAllByOrderByAccountNameAsc(Pageable pageable);

    /**
     * Delete configuration by account name
     */
    void deleteByAccountName(String accountName);
}

