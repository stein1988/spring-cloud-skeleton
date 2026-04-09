package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.StaffAccountProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工账号与权限配置表
 * 对应旧表：lb_location_care.lb_ims_staff（账号权限字段）
 * 职责：敏感账号密码、系统权限配置，最高安全级别
 * 权限控制：仅系统管理员可读写，其他角色无访问权限
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_staff_account")
@EntityProxy
public class StaffAccount extends BaseEntity implements ProxyEntityAvailable<StaffAccount, StaffAccountProxy> {

    /* 已继承BaseEntity字段：
       id、tenantId、createTime、updateTime、isDeleted
     */

    /**
     * 员工ID（关联sys_staff.id）
     */
    private Long staffId;

    /**
     * 职工APP账号唯一标识
     * 对应旧表：lb_ims_staff.account_id
     */
    private String accountId;

    /**
     * IMS平台账号唯一标识
     * 对应旧表：lb_ims_staff.ims_account_id
     */
    private String imsAccountId;

    /**
     * 身份验证账号
     * 对应旧表：lb_ims_staff.verified_account
     */
    private String verifiedAccount;

    /**
     * 身份验证密码
     * 对应旧表：lb_ims_staff.verified_password
     * TODO：BCrypt/Argon2加密存储，禁止明文传输和日志打印
     */
    private String verifiedPassword;

    /**
     * 完成护理设置类型：0=APP直接完成，1=定位到长者房间完成
     * 对应旧表：lb_ims_staff.complete_nursing_setup_type
     * TODO：关联字典表 sys_nursing_setup_type
     */
    private int completeNursingSetupType;

    /**
     * 遗嘱查看权限：0=无权限，1=有权限
     * 对应旧表：lb_ims_staff.will_view_permissions
     * TODO：后续可改为Boolean类型，添加权限控制注解
     */
    private int willViewPermissions;

    /**
     * 是否允许查看库存物资价格：0=不允许，1=允许
     * 对应旧表：lb_ims_staff.allow_view_material_price
     * TODO：后续可改为Boolean类型，添加权限控制注解
     */
    private int allowViewMaterialPrice;
}