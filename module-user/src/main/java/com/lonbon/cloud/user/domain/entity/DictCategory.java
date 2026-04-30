package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.DictCategoryProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.util.List;

@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_dict_category", ignoreProperties = {BaseEntity.Fields.tenantId, BaseEntity.Fields.departmentId})
@EntityProxy
public class DictCategory extends BaseEntity implements ProxyEntityAvailable<DictCategory, DictCategoryProxy> {

    private String code;

    private String name;

    private String description;

    private String parentCode;

    /**
     * 下属的字典分类
     */
    @Navigate(value = RelationTypeEnum.OneToMany, selfProperty = Fields.code, targetProperty =
            DictType.Fields.categoryCode)
    private List<DictType> types;
}
