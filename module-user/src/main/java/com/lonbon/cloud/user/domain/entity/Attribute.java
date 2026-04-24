package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.AttributeProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_attribute", ignoreProperties = {BaseEntity.Fields.departmentId})
@EntityProxy
public class Attribute extends BaseEntity implements ProxyEntityAvailable<Attribute, AttributeProxy> {

    private String entityType;

    private String key;

    private String valueType;

    private String name;

    private String description;


}
