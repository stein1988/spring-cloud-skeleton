package com.lonbon.cloud.base.entity;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.LogicDelete;
import com.easy.query.core.annotation.UpdateIgnore;
import com.easy.query.core.annotation.Version;
import com.easy.query.core.basic.extension.logicdel.LogicDeleteStrategyEnum;
import com.easy.query.core.basic.extension.version.VersionIntStrategy;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

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
@FieldNameConstants
public abstract class BaseEntity implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = -1L;

    /**
     * 主键ID，使用UUIDv7算法
     */
    @Column(comment = "主键ID", primaryKey = true, primaryKeyGenerator = UUIDPrimaryKeyGenerator.class)
    protected UUID id;

    /**
     * 租户ID，可使用EntityProxy(ignoreProperties = "tenantId")来排除此字段
     */
    @Column(comment = "租户ID")
    protected UUID tenantId;

    /**
     * 部门ID，可使用EntityProxy(ignoreProperties = "departmentId")来排除此字段
     */
    @Column(comment = "部门ID")
    protected UUID departmentId;

    /**
     * 预设数据标志：是否是系统预设数据，默认值为false
     */
    @Column(comment = "是否为预设数据")
    protected boolean isPreset;

    /**
     * 逻辑删除标志：是否删除，默认值为false
     * 逻辑删除时，还要处理deleteTime和deleteBy，所以使用自定义策略 {@link DefaultLogicDeleteStrategy}
     */
    @Column(comment = "是否删除")
    @LogicDelete(strategy = LogicDeleteStrategyEnum.CUSTOM, strategyName = "DEFAULT_LOGIC_DELETE_STRATEGY")
    @UpdateIgnore
    protected boolean isDelete;

    /**
     * 删除时间，UTC时间戳
     */
    @Column(comment = "删除时间")
    @UpdateIgnore
    protected OffsetDateTime deleteTime;

    /**
     * 删除人ID
     */
    @Column(comment = "删除人ID")
    @UpdateIgnore
    protected UUID deleteBy;

    /**
     * 创建时间，UTC时间戳
     */
    @Column(comment = "创建时间", nullable = false, dbDefault = "NOW()")
    @UpdateIgnore
    protected OffsetDateTime createTime;

    /**
     * 创建人ID
     */
    @Column(comment = "创建人ID")
    @UpdateIgnore
    protected UUID createBy;

    /**
     * 更新时间，UTC时间戳
     */
    @Column(comment = "更新时间", nullable = false, dbDefault = "NOW()")
    protected OffsetDateTime updateTime;

    /**
     * 更新人ID
     */
    @Column(comment = "更新人ID")
    protected UUID updateBy;

    /**
     * 版本号，用于乐观锁，默认值为0，每更新一次+1
     */
    @Column(comment = "乐观锁版本号")
    @Version(strategy = VersionIntStrategy.class)
    protected int version;

    @Override
    public BaseEntity clone() {
        try {
            return (BaseEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
