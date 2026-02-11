package com.lonbon.cloud.base.entity;

import com.easy.query.core.annotation.Column;
import com.lonbon.cloud.base.config.UUIDPrimaryKeyGenerator;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public abstract class BaseEntity {
    @Column(primaryKey = true, primaryKeyGenerator = UUIDPrimaryKeyGenerator.class)
    private UUID id;

    private OffsetDateTime createdAt;
    private UUID createdBy;
    
    private OffsetDateTime updatedAt;
    private UUID updatedBy;

    private boolean isDeleted;
    private OffsetDateTime deletedAt;
    private UUID deletedBy;
    
    private int versionId;
}
