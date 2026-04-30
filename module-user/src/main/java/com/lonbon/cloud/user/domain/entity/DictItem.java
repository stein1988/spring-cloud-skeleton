package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.DictItemProxy;
import com.lonbon.cloud.user.domain.value_object.DictItemStyle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_dict_item", ignoreProperties = {BaseEntity.Fields.departmentId})
@EntityProxy
public class DictItem extends BaseEntity implements ProxyEntityAvailable<DictItem, DictItemProxy> {

    private String typeId;

    private String code;

    private String name;

    private String description;

    private DictItemStyle style;
}
