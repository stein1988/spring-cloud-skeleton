package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.UserProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 用户
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_user", ignoreProperties = {BaseEntity.Fields.tenantId, BaseEntity.Fields.departmentId})
@EntityProxy
public class User extends BaseEntity implements ProxyEntityAvailable<User, UserProxy> {

    /**
     * 用户名
     * 对应lb_location_care.lb_account.username
     */
    private String username;

    /**
     * 密码哈希
     * 对应lb_location_care.lb_account.password
     */
    private String passwordHash;

    /**
     * 密码盐
     */
    private String passwordSalt;

    /**
     * 当前租户ID，一个用户可以关联多个租户，currentTenantId表示当前登录的租户ID
     */
    private UUID currentTenantId;

    @Navigate(value = RelationTypeEnum.OneToOne, selfProperty = Fields.currentTenantId, targetProperty =
            BaseEntity.Fields.id)
    private Tenant currentTenant;

    @Navigate(value = RelationTypeEnum.ManyToMany, mappingClass = UserRole.class, selfProperty = BaseEntity.Fields.id
            , selfMappingProperty = UserRole.Fields.userId, targetMappingProperty = UserRole.Fields.roleId,
            targetProperty = BaseEntity.Fields.id, subQueryToGroupJoin = true)
    private List<Role> roles;

    /**
     * 当前团队ID，一个用户可以关联多个团队，currentTeamId表示当前登录的团队ID
     */
    private UUID currentTeamId;

    /**
     * 是否超级管理员
     */
    private Boolean isSuperAdmin;

    /**
     * 是否激活
     */
    @Column(dbDefault = "true")
    private Boolean isActive;

    /**
     * 最后登录时间
     */
    private OffsetDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 账号类型
     * 对应lb_location_care.lb_account.account_type
     */
    private String type;

    /**
     * 姓名
     * 对应lb_location_care.lb_account.display_name
     */
    private String name;

    /**
     * 账号描述
     * 对应lb_location_care.lb_account.account_desc
     */
    private String description;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     * 对应lb_location_care.lb_account.phone_num
     */
    private String phone;

    /**
     * 头像URL
     * 对应lb_location_care.lb_account.head_image
     */
    private String avatarUrl;

    /**
     * 时区
     */
    private String timezone;

    /**
     * 语言
     */
    private String language;
}


// CREATE TABLE `lb_account` (
//   `account_id` varchar(64) NOT NULL DEFAULT '' COMMENT '主键',
//   `username` varchar(128) DEFAULT '' COMMENT '登录用户名',
//   `password` varchar(32) DEFAULT '' COMMENT '强密码',
//   `display_name` varchar(128) DEFAULT NULL COMMENT '姓名',
//   `phone_num` varchar(32) DEFAULT '' COMMENT '登录手机号',
//   `account_type` int(2) NOT NULL DEFAULT '3' COMMENT '账号类型，参见字典表',
//   `account_desc` varchar(32) DEFAULT '' COMMENT '描述信息',
//   `head_image` varchar(255) DEFAULT '' COMMENT '头像地址',
//   `parent_account_id` varchar(64) DEFAULT '' COMMENT '用户创建者的用户id(项目用户使用)',
//   `account_level` tinyint(1) DEFAULT '0' COMMENT '预留字段(用户层级，用于项目允许子项目的情况)',
//   `approve_phone` varchar(32) DEFAULT '' COMMENT '审批通知手机号',
//   `production_line` varchar(32) DEFAULT '' COMMENT '生产线',
//   `is_delete` tinyint(1) DEFAULT '0' COMMENT '是否删除 0 否 1 是',
//   `create_time` bigint(20) DEFAULT '0' COMMENT '创建时间',
//   `update_time` bigint(20) DEFAULT '0' COMMENT '更新时间',
//   `is_login_app` tinyint(1) DEFAULT '0' COMMENT '是否可以登录app，0否，1是',
//   `real_name` varchar(64) DEFAULT '' COMMENT '真实姓名',
//   `is_whitelist` tinyint(1) DEFAULT '0' COMMENT '是否是白名单，0否，1是',
//   `device_model` varchar(64) DEFAULT '' COMMENT '使用子女app的用户所在地',
//   `os_version` varchar(64) DEFAULT '' COMMENT '使用子女app的手机信息',
//   `ip` varchar(64) DEFAULT '' COMMENT '下载子女app安装包的ip ',
//   `app_version_code` varchar(64) DEFAULT NULL COMMENT '软件版本号',
//   `open_id` varchar(32) DEFAULT '' COMMENT '关联微信账号id',
//   `weak_password` varchar(32) DEFAULT '' COMMENT '弱密码',
//   `theme_color` tinyint(1) DEFAULT '1' COMMENT '网页导航栏皮肤',
//   `approved_result` tinyint(1) DEFAULT '1' COMMENT '审批结果 0待审批 1通过 2未通过',
//   `device_tag` text COMMENT '设备登录唯一标记',
//   `password_change_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '密码修改时间',
//   `username_is_legal` tinyint(1) NOT NULL DEFAULT '1' COMMENT '账号合法标识（1-合法，2-非法）',
//   `password_is_legal` tinyint(1) NOT NULL DEFAULT '1' COMMENT '密码合法标识（1-合法，2-非法）',
//   `health_station_id` varchar(32) DEFAULT NULL COMMENT '卫生站id',
//   `suit_aged_manage_id` varchar(32) DEFAULT NULL COMMENT '适老改造项目（包）id',
//   `is_administrators` tinyint(1) DEFAULT NULL COMMENT '是否是项目管理员账号',
//   `notice_is_display` tinyint(1) NOT NULL DEFAULT '1' COMMENT '新通知是否显示 0 否 1 是',
//   `alias_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '极光别名类型（1-用户名，2-用户标识）',
//   `attendance_model` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1-简易护理模式 2-默认护理模式',
//   `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认：0-否 1-是',
//   `is_init` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否初始：0-否 1-是',
//   PRIMARY KEY (`account_id`),
//   KEY `username` (`username`) USING BTREE,
//   KEY `idx_phone_num` (`phone_num`) USING BTREE,
//   KEY `idx_account_type` (`account_type`) USING BTREE
// ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT COMMENT='系统账号表';