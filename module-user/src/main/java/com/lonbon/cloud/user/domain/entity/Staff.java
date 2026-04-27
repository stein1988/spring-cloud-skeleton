package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.base.service.AttributeEntity;
import com.lonbon.cloud.user.domain.entity.proxy.StaffProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
import java.util.List;

/**
 * 员工实体类
 * <p>
 * 员工是系统中的一种实体，用于人事管理，有区别与用户user。
 * 每个员工都有唯一的工号、姓名、所属部门组织、在职状态、入职日期、离职日期、主管状态等信息。
 * </p>
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_staff")
@EntityProxy
public class Staff extends BaseEntity implements ProxyEntityAvailable<Staff, StaffProxy> {

    /* tenantId、departmentId 在 BaseEntity 中 */

    /* 已继承BaseEntity字段：
       id(主键)、tenantId、departmentId、createTime、updateTime、isDeleted
     */

    /**
     * 原系统员工唯一标识（兼容旧数据）
     * 对应旧表：lb_ims_staff.staff_id
     */
    private String originalStaffId;

    /**
     * 工号（内部唯一）
     * 对应旧表：lb_ims_staff.code
     * TODO：添加数据库唯一索引 uk_staff_no
     */
    private String staffNo;

    /**
     * 机构主键
     * 对应旧表：lb_ims_staff.org_id
     */
    private String orgId;

    /**
     * 所属部门组织ID
     * 对应旧表：lb_ims_staff.department_framework_id
     */
    private String departmentFrameworkId;

    /**
     * 姓名
     * 对应旧表：lb_ims_staff.name
     */
    private String name;

    /**
     * 在职状态：0=在职，1=离职
     * 对应旧表：lb_ims_staff.status
     * TODO：关联字典表 sys_staff_status，添加枚举
     */
    private Integer status;

    /**
     * 入职日期
     * 对应旧表：lb_ims_staff.entry_time
     */
    private LocalDate hireDate;

    /**
     * 离职日期
     * 对应旧表：lb_ims_staff.dimission_time
     */
    private LocalDate resignDate;

    /**
     * 是否是主管：0=否，1=是
     * 对应旧表：lb_ims_staff.is_superior
     * TODO：后续可改为 Boolean 类型
     */
    private Integer isSuperior;

    @Navigate(value = RelationTypeEnum.OneToMany, selfProperty = BaseEntity.Fields.id, targetProperty =
            AttributeEntity.Fields.entityId)
    private List<StaffAttribute> attributes;

    // ==================== 一对一关联扩展表 ====================
    /**
     * 个人基本信息
     */
    @Navigate(value = RelationTypeEnum.OneToOne, targetProperty = "staffId")
    private StaffProfile profile;

    /**
     * 雇佣与合同信息
     */
    @Navigate(value = RelationTypeEnum.OneToOne, targetProperty = "staffId")
    private StaffEmployment employment;

    /**
     * 账号与权限配置
     */
    @Navigate(value = RelationTypeEnum.OneToOne, targetProperty = "staffId")
    private StaffAccount account;
}

//    /**
//     * 编号，外部系统对接字段
//     * 对应lb_location_care.lb_ims_staff.code
//     */
//    private String code;
//
//    /**
//     * 类型
//     */
//    private String type;
//
//    /**
//     * 姓名
//     * 对应lb_location_care.lb_ims_staff.name
//     */
//    private String name;
//
//    /**
//     * 证件类型
//     * TODO：证件枚举、以及字典枚举设计
//     */
//    private int certType;
//
//    /**
//     * 证件号码
//     * 对应lb_location_care.lb_ims_staff.identity
//     * TODO：身份证号校验
//     */
//    private String certNumber;
//
//    /**
//     * 性别，0 = 未知 / 保密、1 = 男、2 = 女
//     * 对应lb_location_care.lb_ims_staff.gender
//     * TODO：性别枚举、以及默认值
//     */
//    private int gender;
//
//    /**
//     * 出生日期，数据库字段为Date类型
//     * 对应lb_location_care.lb_ims_staff.birthday
//     * TODO：建立字段规范skill，日期用XXX_date，日期+时间用XXX_time
//     */
//    private LocalDate birthDate;
//
//    /**
//     * 民族，比nation语义更精确
//     * 对应lb_location_care.lb_ims_staff.nation
//     */
//    private String ethnicity;
//
//    /**
//     * 政治面貌
//     * 对应lb_location_care.lb_ims_staff.political_status
//     */
//    private String politicalStatus;
//
//    /**
//     * 籍贯
//     * 对应lb_location_care.lb_ims_staff.political_status
//     */
//    private String nativePlace;
//}

/*
CREATE TABLE `lb_ims_staff` (
  -- `staff_id` varchar(32) NOT NULL COMMENT '主键',
  -- `org_id` varchar(32) NOT NULL DEFAULT '' COMMENT '机构主键',
  -- `department_id` varchar(32) NOT NULL DEFAULT '' COMMENT '部门主键',
  -- `name` varchar(125) NOT NULL COMMENT '姓名',
  -- `identity` varchar(32) DEFAULT '' COMMENT '身份证号',
  -- `gender` char(1) DEFAULT NULL COMMENT '性别',
  `age` tinyint(3) DEFAULT '0' COMMENT '年龄',
  `phone_num` varchar(11) NOT NULL DEFAULT '' COMMENT '联系电话',
  `emergency_contact_phone` varchar(11) DEFAULT '' COMMENT '紧急联系人电话',
  -- `nation` char(2) DEFAULT NULL COMMENT '民族',
  -- `political_status` char(1) DEFAULT NULL COMMENT '政治面貌',
  -- `hometown` varchar(32) DEFAULT '' COMMENT '籍贯',
  `job_title` varchar(32) DEFAULT NULL COMMENT '职称',
  `census_address_code` varchar(125) DEFAULT '' COMMENT '户籍地址行政区码',
  `census_address` varchar(125) DEFAULT '' COMMENT '户籍地址',
  `home_address_code` varchar(125) DEFAULT '' COMMENT '家庭地址行政区码',
  `home_address` varchar(125) DEFAULT '' COMMENT '家庭地址',
  `educational_level` varchar(32) NOT NULL COMMENT '文化程度',
  `specialty` varchar(32) DEFAULT '' COMMENT '专业',
  `job_title_level` char(1) DEFAULT NULL COMMENT '职称级别',
  `other_treatment` varchar(200) DEFAULT '' COMMENT '其他待遇',
  `office` varchar(32) DEFAULT NULL COMMENT '职务',
  `salary` varchar(32) DEFAULT '' COMMENT '薪金',
  `contract_type` char(1) DEFAULT NULL COMMENT '合同类型',
  `contract_num` varchar(32) DEFAULT '' COMMENT '合同编号',
  `contract_start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '合同开始时间',
  `contract_end_update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '合同结束时间',
  `entry_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '入职时间',
  `personnel_type` varchar(32) NOT NULL COMMENT '人员类型0-其他人员类型1-管理人员2-财务人员3-安全人员4-后勤人员5-护理人员6-医务人员7-人事人员８-管家人员',
  `job_position` varchar(32) DEFAULT NULL COMMENT '工作岗位',
  `contract_attachment` varchar(255) DEFAULT '' COMMENT '合同附件',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '在职状态（0 在职，1 离职）',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除（0 未删除，1 已删除）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `photo` varchar(255) DEFAULT '' COMMENT '头像',
  `contract_attachment_name` varchar(255) DEFAULT '' COMMENT '合同附件名称',
  `account_id` varchar(32) DEFAULT '' COMMENT '职工app账号唯一标识',
  `ims_account_id` varchar(32) DEFAULT '' COMMENT 'ims平台帐号唯一标识',
  -- `birthday` bigint(20) DEFAULT '0' COMMENT '生日',
  -- `code` varchar(100) DEFAULT NULL COMMENT '人员编号',
  `dimission_time` bigint(20) DEFAULT NULL COMMENT '离职时间',
  `department_framework_id` varchar(32) DEFAULT '' COMMENT '所属部门组织id',
  `is_superior` tinyint(1) DEFAULT '0' COMMENT '是否是主管 0不是 1是',
  `kingdee_expense_department` varchar(32) NOT NULL DEFAULT '' COMMENT '金蝶部门编码',
  `verified_account` varchar(255) NOT NULL DEFAULT '' COMMENT '身份验证账号',
  `verified_password` varchar(255) NOT NULL DEFAULT '' COMMENT '身份验证密码',
  `complete_nursing_setup_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '完成护理设置类型 0-APP直接完成 1-定位到长者房间完成',
  `will_view_permissions` tinyint(1) NOT NULL DEFAULT '0' COMMENT '遗嘱查看权限 0-无权限 1-有权限',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认：0-否 1-是',
  `is_init` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否初始：0-否 1-是',
  `allow_view_material_price` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否允许查看库存物资价格 0-不允许 1-允许',
  PRIMARY KEY (`staff_id`) USING BTREE,
  KEY `department_id` (`department_id`) USING BTREE,
  KEY `org_id` (`org_id`,`personnel_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='员工表';



 */