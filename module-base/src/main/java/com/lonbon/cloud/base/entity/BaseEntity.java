package com.lonbon.cloud.base.entity;

import com.easy.query.core.annotation.Column;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public abstract class BaseEntity {
    @Column(primaryKey = true)
    private UUID id;
    
    private boolean isDeleted;
    private LocalDateTime deletedAt;
    private UUID deletedBy;
    
    private LocalDateTime createdAt;
    private UUID createdBy;
    
    private LocalDateTime updatedAt;
    private UUID updatedBy;
    
    private int versionId;
}
