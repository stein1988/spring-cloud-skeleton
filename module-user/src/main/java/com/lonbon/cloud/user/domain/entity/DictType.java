package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.DictTypeProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_dict_type", ignoreProperties = {BaseEntity.Fields.departmentId})
@EntityProxy
public class DictType extends BaseEntity implements ProxyEntityAvailable<DictType, DictTypeProxy> {

    private String categoryId;

    private String name;

    private String description;

    /**
     * 下属的字典选项
     */
    @Navigate(value = RelationTypeEnum.OneToMany, selfProperty = BaseEntity.Fields.id, targetProperty =
            DictItem.Fields.typeId)
    private List<DictItem> items;
}
