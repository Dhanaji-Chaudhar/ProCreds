package com.procreds.repository;

import com.procreds.entity.AzureAksConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AzureAksConfigRepository extends MongoRepository<AzureAksConfig, String> {

    /**
     * Find configuration by account name
     */
    Optional<AzureAksConfig> findByAccountName(String accountName);

    /**
     * Check if configuration exists by account name
     */
    boolean existsByAccountName(String accountName);

    /**
     * Find configurations by cluster name
     */
    Page<AzureAksConfig> findByClusterNameContainingIgnoreCase(String clusterName, Pageable pageable);

    /**
     * Find configurations by tenant ID
     */
    Page<AzureAksConfig> findByTenantId(String tenantId, Pageable pageable);

    /**
     * Find configurations by client ID
     */
    Page<AzureAksConfig> findByClientId(String clientId, Pageable pageable);

    /**
     * Search configurations by account name, cluster name, tenant ID, client ID, or description
     */
    @Query("{ $or: [ " +
           "{ 'accountName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'clusterName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'tenantId': { $regex: ?0, $options: 'i' } }, " +
           "{ 'clientId': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<AzureAksConfig> searchConfigurations(String searchTerm, Pageable pageable);

    /**
     * Find all configurations ordered by account name
     */
    Page<AzureAksConfig> findAllByOrderByAccountNameAsc(Pageable pageable);

    /**
     * Delete configuration by account name
     */
    void deleteByAccountName(String accountName);
}

