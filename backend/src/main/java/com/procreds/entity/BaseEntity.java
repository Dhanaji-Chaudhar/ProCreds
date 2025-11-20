package com.procreds.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Base entity class providing common fields for all entities
 * 
 * Provides:
 * - Unique identifier (id)
 * - Audit timestamps (createdAt, updatedAt)
 * - Automatic timestamp management via Spring Data MongoDB auditing
 * 
 * @author ProCreds Team
 */
@Data
public abstract class BaseEntity {

    @Id
    private String id;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
}

