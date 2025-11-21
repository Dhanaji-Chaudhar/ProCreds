package com.procreds.repository;

import com.procreds.entity.AwsEksConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AwsEksConfigRepository extends MongoRepository<AwsEksConfig, String> {

    /**
     * Find configuration by account name
     */
    Optional<AwsEksConfig> findByAccountName(String accountName);

    /**
     * Check if configuration exists by account name
     */
    boolean existsByAccountName(String accountName);

    /**
     * Find configurations by cluster name
     */
    Page<AwsEksConfig> findByClusterNameContainingIgnoreCase(String clusterName, Pageable pageable);

    /**
     * Find configurations by region
     */
    Page<AwsEksConfig> findByRegion(String region, Pageable pageable);

    /**
     * Find configurations by access key
     */
    Page<AwsEksConfig> findByAccessKeyContainingIgnoreCase(String accessKey, Pageable pageable);

    /**
     * Search configurations by account name, cluster name, region, or description
     */
    @Query("{ $or: [ " +
           "{ 'accountName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'clusterName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'region': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<AwsEksConfig> searchConfigurations(String searchTerm, Pageable pageable);

    /**
     * Find all configurations ordered by account name
     */
    Page<AwsEksConfig> findAllByOrderByAccountNameAsc(Pageable pageable);

    /**
     * Delete configuration by account name
     */
    void deleteByAccountName(String accountName);
}

