package com.lonbon.cloud.base.entity;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.LogicDelete;
import com.easy.query.core.annotation.UpdateIgnore;
import com.easy.query.core.annotation.Version;
import com.easy.query.core.basic.extension.logicdel.LogicDeleteStrategyEnum;
import com.easy.query.core.basic.extension.version.VersionIntStrategy;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 参考<a href="https://www.easy-query.com/easy-query-doc/practice/configuration/entity.html">easy-query文档-对象设计</a>
 * 主键id使用UUIDv7、自动生成 {@link UUIDPrimaryKeyGenerator}
 * create、update相关字段自动设置 {@link DefaultEntityInterceptor}
 * delete相关字段自动设置、查询过滤器 {@link DefaultLogicDeleteStrategy}
 * 时间戳使用带时区的UTC时间戳，代替默认的LocalDateTime {@link OffsetDateTimeTypeHandler}
 * 乐观锁版本号使用int类型，默认值为0，每更新一次+1，参考<a href="https://www.easy-query.com/easy-query-doc/adv/version.html">
 */

@Data
public abstract class BaseEntity implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = -1L;

    /**
     * 主键ID，使用UUIDv7算法
     */
    @Column(primaryKey = true, primaryKeyGenerator = UUIDPrimaryKeyGenerator.class)
    private UUID id;

    /**
     * 逻辑删除标志：是否删除，默认值为false
     * 逻辑删除时，还要处理deleteTime和deleteBy，所以使用自定义策略 {@link DefaultLogicDeleteStrategy}
     */
    @LogicDelete(strategy = LogicDeleteStrategyEnum.CUSTOM, strategyName = "DEFAULT_LOGIC_DELETE_STRATEGY")
    @UpdateIgnore
    private boolean isDeleted = false;

    /**
     * 删除时间，UTC时间戳
     */
    @UpdateIgnore
    private OffsetDateTime deletedAt;

    /**
     * 删除人ID
     */
    @UpdateIgnore
    private UUID deletedBy;

    /**
     * 创建时间，UTC时间戳
     */
    @UpdateIgnore
    private OffsetDateTime createdAt;
    
    /**
     * 创建人ID
     */
    @UpdateIgnore
    private UUID createdBy;
    
    /**
     * 更新时间，UTC时间戳
     */
    private OffsetDateTime updatedAt;
    
    /**
     * 更新人ID
     */
    private UUID updatedBy;

    /**
     * 版本号，用于乐观锁，默认值为0，每更新一次+1
     */
    @Version(strategy = VersionIntStrategy.class)
    private int version;

    @Override
    public BaseEntity clone() {
        try {
            BaseEntity clone = (BaseEntity) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
