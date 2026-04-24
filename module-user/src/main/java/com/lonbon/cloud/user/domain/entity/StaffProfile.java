package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.StaffProfileProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;

/**
 * 员工个人基本信息表
 * 对应旧表：lb_location_care.lb_ims_staff（个人信息字段）
 * 职责：个人身份、联系方式、地址等隐私信息
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_staff_profile")
@EntityProxy
public class StaffProfile extends BaseEntity implements ProxyEntityAvailable<StaffProfile, StaffProfileProxy> {

    /* 已继承BaseEntity字段：
       id、tenantId、createTime、updateTime、isDeleted
     */

    /**
     * 员工ID（关联sys_staff.id）
     */
    private Long staffId;

    /**
     * 证件类型
     * 对应旧表：无（原identity默认身份证）
     * TODO：关联字典表 sys_cert_type，添加证件枚举
     */
    private Integer certType;

    /**
     * 证件号码
     * 对应旧表：lb_ims_staff.identity
     * TODO：添加身份证号格式校验，国密SM4加密存储
     */
    private String certNumber;

    /**
     * 性别：0=未知/保密，1=男，2=女
     * 对应旧表：lb_ims_staff.gender
     * TODO：关联字典表 sys_gender，添加枚举，默认值0
     */
    private Integer gender;

    /**
     * 出生日期
     * 对应旧表：lb_ims_staff.birthday
     * TODO：已删除旧表age字段，由birthDate实时计算周岁
     */
    private LocalDate birthDate;

    /**
     * 民族编码（GB/T 3304-1991）
     * 对应旧表：lb_ims_staff.nation
     * TODO：关联字典表 sys_ethnicity，添加常用民族枚举
     */
    private Integer ethnicity;

    /**
     * 政治面貌编码（GB/T 4762-1984）
     * 对应旧表：lb_ims_staff.political_status
     * TODO：关联字典表 sys_political_status，添加常用政治面貌枚举
     */
    private Integer politicalStatus;

    /**
     * 籍贯
     * 对应旧表：lb_ims_staff.hometown
     * TODO：后续拆分为省市区三级编码+冗余完整名称
     */
    private String nativePlace;

    /**
     * 联系电话
     * 对应旧表：lb_ims_staff.phone_num
     * TODO：添加手机号格式校验
     */
    private String phone;

    /**
     * 紧急联系人电话
     * 对应旧表：lb_ims_staff.emergency_contact_phone
     */
    private String emergencyContactPhone;

    /**
     * 户籍地址行政区码
     * 对应旧表：lb_ims_staff.census_address_code
     * TODO：关联行政区划字典表 sys_region
     */
    private String censusAddressCode;

    /**
     * 户籍地址
     * 对应旧表：lb_ims_staff.census_address
     */
    private String censusAddress;

    /**
     * 家庭地址行政区码
     * 对应旧表：lb_ims_staff.home_address_code
     * TODO：关联行政区划字典表 sys_region
     */
    private String homeAddressCode;

    /**
     * 家庭地址
     * 对应旧表：lb_ims_staff.home_address
     */
    private String homeAddress;

    /**
     * 文化程度编码
     * 对应旧表：lb_ims_staff.educational_level
     * TODO：关联字典表 sys_educational_level
     */
    private Integer educationalLevel;

    /**
     * 专业
     * 对应旧表：lb_ims_staff.specialty
     */
    private String specialty;

    /**
     * 头像URL
     * 对应旧表：lb_ims_staff.photo
     */
    private String photo;
}