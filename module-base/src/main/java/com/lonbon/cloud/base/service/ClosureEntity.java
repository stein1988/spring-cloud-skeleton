package com.lonbon.cloud.base.service;

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
public abstract class ClosureEntity extends BaseEntity {

    /**
     * 祖先租户ID
     */
    private UUID ancestorId;

    /**
     * 后代租户ID
     */
    private UUID descendantId;

    /**
     * 层级距离，取值>=0，不能为负数，0表示自身，ancestorId=descendantId，1表示直接后代
     */
    private Integer distance;
}
