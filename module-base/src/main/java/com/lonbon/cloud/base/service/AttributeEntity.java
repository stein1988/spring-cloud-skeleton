package com.lonbon.cloud.base.service;

import com.lonbon.cloud.base.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@NullUnmarked
public abstract class AttributeEntity extends BaseEntity {

    private @NonNull UUID entityId;

    private @NonNull UUID attributeId;

    private @NonNull String key;

    private String stringValue;

    private Integer intValue;

    private Long longValue;

    private BigDecimal decimalValue;

    private Boolean booleanValue;
}
