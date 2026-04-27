package com.lonbon.cloud.base.service;

import com.easy.query.core.annotation.SaveKey;
import com.lonbon.cloud.base.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.jspecify.annotations.NullUnmarked;

import java.util.UUID;

@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@NullUnmarked
public abstract class AttributeEntity extends BaseEntity {

    private UUID entityId;

    private UUID attributeId;

    @SaveKey
    private String key;

    private String value;

    private String stringValue;

    private Integer intValue;

    private Long longValue;

//    private BigDecimal decimalValue;

    private Boolean booleanValue;
}
