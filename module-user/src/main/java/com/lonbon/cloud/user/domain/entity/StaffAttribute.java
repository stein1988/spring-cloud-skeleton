package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.base.service.AttributeEntity;
import com.lonbon.cloud.user.domain.entity.proxy.StaffAttributeProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_staff_attribute", ignoreProperties = {BaseEntity.Fields.departmentId})
@EntityProxy
public class StaffAttribute extends AttributeEntity
        implements ProxyEntityAvailable<StaffAttribute, StaffAttributeProxy> {

}
