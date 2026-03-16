package com.lonbon.cloud.base.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class BaseBizEntity extends BaseEntity {
    private UUID tenantId;
    private UUID teamId;
}
