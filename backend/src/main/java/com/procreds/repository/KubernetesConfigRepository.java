package com.procreds.repository;

import com.procreds.entity.KubernetesConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KubernetesConfigRepository extends MongoRepository<KubernetesConfig, String> {

    /**
     * Find configuration by account name
     */
    Optional<KubernetesConfig> findByAccountName(String accountName);

    /**
     * Check if configuration exists by account name
     */
    boolean existsByAccountName(String accountName);

    /**
     * Find configurations by cluster name
     */
    Page<KubernetesConfig> findByClusterNameContainingIgnoreCase(String clusterName, Pageable pageable);

    /**
     * Find configurations by namespace
     */
    Page<KubernetesConfig> findByNamespaceContainingIgnoreCase(String namespace, Pageable pageable);

    /**
     * Find configurations by region
     */
    Page<KubernetesConfig> findByRegionContainingIgnoreCase(String region, Pageable pageable);

    /**
     * Search configurations by account name, cluster name, namespace, region, or description
     */
    @Query("{ $or: [ " +
           "{ 'accountName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'clusterName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'namespace': { $regex: ?0, $options: 'i' } }, " +
           "{ 'region': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<KubernetesConfig> searchConfigurations(String searchTerm, Pageable pageable);

    /**
     * Find all configurations ordered by account name
     */
    Page<KubernetesConfig> findAllByOrderByAccountNameAsc(Pageable pageable);

    /**
     * Delete configuration by account name
     */
    void deleteByAccountName(String accountName);
}

