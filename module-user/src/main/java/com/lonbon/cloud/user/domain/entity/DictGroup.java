package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.DictGroupProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_dict_group", ignoreProperties = {BaseEntity.Fields.tenantId, BaseEntity.Fields.departmentId})
@EntityProxy
public class DictGroup extends BaseEntity implements ProxyEntityAvailable<DictGroup, DictGroupProxy> {

    private String name;

    private String description;

    private String parentId;

    /**
     * 下属的字典分类
     */
    @Navigate(value = RelationTypeEnum.OneToMany, selfProperty = BaseEntity.Fields.id, targetProperty =
            DictType.Fields.categoryId)
    private List<DictType> types;
}
