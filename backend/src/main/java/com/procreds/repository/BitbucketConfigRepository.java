package com.procreds.repository;

import com.procreds.entity.BitbucketConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BitbucketConfigRepository extends MongoRepository<BitbucketConfig, String> {

    /**
     * Find configuration by username
     */
    Optional<BitbucketConfig> findByUsername(String username);

    /**
     * Check if configuration exists by username
     */
    boolean existsByUsername(String username);

    /**
     * Find configurations by workspace
     */
    Page<BitbucketConfig> findByWorkspaceContainingIgnoreCase(String workspace, Pageable pageable);

    /**
     * Search configurations by username, workspace, or description
     */
    @Query("{ $or: [ " +
           "{ 'username': { $regex: ?0, $options: 'i' } }, " +
           "{ 'workspace': { $regex: ?0, $options: 'i' } }, " +
           "{ 'description': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<BitbucketConfig> searchConfigurations(String searchTerm, Pageable pageable);

    /**
     * Find all configurations ordered by username
     */
    Page<BitbucketConfig> findAllByOrderByUsernameAsc(Pageable pageable);

    /**
     * Delete configuration by username
     */
    void deleteByUsername(String username);
}

