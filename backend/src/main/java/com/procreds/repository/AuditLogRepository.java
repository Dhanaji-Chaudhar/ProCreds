package com.procreds.repository;

import com.procreds.entity.AuditLog;
import com.procreds.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    Page<AuditLog> findByUser(User user, Pageable pageable);
    
    Page<AuditLog> findByAction(String action, Pageable pageable);
    
    Page<AuditLog> findByPlatform(String platform, Pageable pageable);
    
    Page<AuditLog> findByResult(AuditLog.ActionResult result, Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    Page<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate, 
                                  Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.user.id = :userId AND a.createdAt BETWEEN :startDate AND :endDate")
    Page<AuditLog> findByUserAndDateRange(@Param("userId") Long userId,
                                         @Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate,
                                         Pageable pageable);
    
    @Query("SELECT a FROM AuditLog a WHERE a.platform = :platform AND a.createdAt BETWEEN :startDate AND :endDate")
    Page<AuditLog> findByPlatformAndDateRange(@Param("platform") String platform,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate,
                                             Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.action = :action AND a.createdAt > :since")
    long countByActionSince(@Param("action") String action, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.result = :result AND a.createdAt > :since")
    long countByResultSince(@Param("result") AuditLog.ActionResult result, @Param("since") LocalDateTime since);
    
    @Query("SELECT a.platform, COUNT(a) FROM AuditLog a WHERE a.createdAt > :since GROUP BY a.platform")
    List<Object[]> countByPlatformSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT a.action, COUNT(a) FROM AuditLog a WHERE a.createdAt > :since GROUP BY a.action")
    List<Object[]> countByActionSince(@Param("since") LocalDateTime since);
}

