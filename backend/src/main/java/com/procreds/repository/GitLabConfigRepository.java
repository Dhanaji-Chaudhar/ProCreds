package com.procreds.repository;

import com.procreds.entity.GitLabConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GitLabConfigRepository extends MongoRepository<GitLabConfig, String> {

    /**
     * Find configuration by account name
     */
    Optional<GitLabConfig> findByAccountName(String accountName);

    /**
     * Check if configuration exists by account name
     */
    boolean existsByAccountName(String accountName);

    /**
     * Find configurations by group ID
     */
    Page<GitLabConfig> findByGroupId(String groupId, Pageable pageable);

    /**
     * Search configurations by account name, group ID, or description
     */
    @Query("{ $or: [ " +
           "{ 'accountName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'groupId': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<GitLabConfig> searchConfigurations(String searchTerm, Pageable pageable);

    /**
     * Find all configurations ordered by account name
     */
    Page<GitLabConfig> findAllByOrderByAccountNameAsc(Pageable pageable);

    /**
     * Delete configuration by account name
     */
    void deleteByAccountName(String accountName);
}

