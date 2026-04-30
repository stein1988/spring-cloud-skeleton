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

    /**
     * 归属业务实体
     */
    private String targetEntity;

    /**
     * 属性分类
     */
    private String category;

    /**
     * 属性唯一键
     */
    private String key;
    
    /**
     * 值类型：string/int/date/json/bool
     */
    private String valueType;

    /**
     * 属性名称
     */
    private String name;

    /**
     * 属性描述
     */
    private String description;

    /**
     * 排序序号
     */
    private Integer sort;

    /**
     * 是否必填 0否 1是
     */
    private Integer required;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 系统字典编码
     * 作用：下拉 / 单选 / 多选类型属性，绑定系统字典，快速实现枚举下拉
     */
    private String dictCode;

    /**
     * 自定义选项配置(JSON)
     * 作用：非系统字典场景，自定义选项（单选、多选、复选）JSON 配置
     */
    private String optionConfig;

    /**
     * 校验规则(正则/规则描述)
     * 作用：正则、长度、范围校验，如手机号、邮箱、数字范围
     */
    private String verifyRule;

    /**
     * 状态 0禁用 1正常
     */
    private Integer status;

    /**
     * 表单组件类型 input/select/date等
     */
    private String formType;

    /**
     * 输入框占位提示
     */
    private String placeholder;

    /**
     * 表单栅格宽度
     */
    private Integer span;
}
