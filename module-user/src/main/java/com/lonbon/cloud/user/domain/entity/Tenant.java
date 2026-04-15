package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.*;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.base.service.ClosureAvailable;
import com.lonbon.cloud.user.domain.entity.proxy.TenantProxy;
import com.lonbon.cloud.user.domain.filter.TenantClosureFilter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.util.List;
import java.util.UUID;

/**
 * 租户
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_tenant", ignoreProperties = {"tenantId", "departmentId"})
@EntityProxy
public class Tenant extends BaseEntity
        implements ProxyEntityAvailable<Tenant, TenantProxy>, ClosureAvailable<TenantClosure> {

    /**
     * 类型
     * 对应lb_location_care.lb_organization.org_type
     * TODO：确定字典表的意义
     */
    private String type;

    /**
     * 名称
     * 对应lb_location_care.lb_organization.org_name
     */
    private String name;

    /**
     * 描述
     * 对应lb_location_care.lb_organization.org_desc
     */
    private String description;

    /**
     * 是否默认团队
     */
    private Boolean isDefault;

    /**
     * 是否激活
     * 对应lb_location_care.lb_organization.is_use
     */
    @Column(dbDefault = "true")
    private Boolean isActive;

    private UUID parentId;

    /**
     * 祖先列表
     */
    @Navigate(value = RelationTypeEnum.OneToMany, selfProperty = {"id"}, targetProperty = {"descendantId"},
            orderByProps = @OrderByProperty(property = "distance"), extraFilter = TenantClosureFilter.class)
    private List<TenantClosure> ancestors;

    /**
     * 后代列表
     */
    @Navigate(value = RelationTypeEnum.OneToMany, selfProperty = {"id"}, targetProperty = {"ancestorId"},
            orderByProps = @OrderByProperty(property = "distance"), extraFilter = TenantClosureFilter.class)
    private List<TenantClosure> descendants;

//    @Override
//    public TenantClosure createClosure(UUID ancestorId, UUID descendantId, int distance) {
//        return new TenantClosure(ancestorId, descendantId, distance);
//    }
}

/**
 * 地址
 * 对应lb_location_care.lb_organization.org_address
 */
// private String address;

/**
 * 行政区划ID
 * 对应lb_location_care.lb_organization.region_id
 * TODO：确定和address字段的关系，确认是否要冗余region_desc
 */
// private String region_id;

/**
 * 经度
 * 对应lb_location_care.lb_organization.longitude
 */
// private String longitude;

/**
 * 纬度
 * 对应lb_location_care.lb_organization.latitude
 */
// private String latitude;

/**
 * 域名
 * 对应rouyi_cloud_plus.sys_tenant.domain
 */
//    private String domain;


//    contact_user_name varchar(20)   default null::varchar,
//    contact_phone     varchar(20)   default null::varchar,
//    company_name      varchar(30)   default null::varchar,
//    license_number    varchar(30)   default null::varchar,
//    address           varchar(200)  default null::varchar,
//    intro             varchar(200)  default null::varchar,
//    domain            varchar(200)  default null::varchar,
//    remark            varchar(200)  default null::varchar,
//    package_id        int8,
//    expire_time       timestamp,
//    account_count     int4          default -1,
//    status            char          default '0'::bpchar,
//    del_flag          char          default '0'::bpchar,
//    create_dept       int8,
//    create_by         int8,
//    create_time       timestamp,
//    update_by         int8,
//    update_time       timestamp,
//    constraint "pk_sys_tenant" primary key (id)
//);
//
//
//    comment on table   sys_tenant                    is '租户表';
//    comment on column  sys_tenant.tenant_id          is '租户编号';
//    comment on column  sys_tenant.contact_phone      is '联系电话';
//    comment on column  sys_tenant.company_name       is '企业名称';
//    comment on column  sys_tenant.company_name       is '联系人';
//    comment on column  sys_tenant.license_number     is '统一社会信用代码';
//    comment on column  sys_tenant.address            is '地址';
//    comment on column  sys_tenant.intro              is '企业简介';
//    comment on column  sys_tenant.domain             is '域名';
//    comment on column  sys_tenant.remark             is '备注';
//    comment on column  sys_tenant.package_id         is '租户套餐编号';
//    comment on column  sys_tenant.expire_time        is '过期时间';
//    comment on column  sys_tenant.account_count      is '用户数量（-1不限制）';
//    comment on column  sys_tenant.status             is '租户状态（0正常 1停用）';
//    comment on column  sys_tenant.del_flag           is '删除标志（0代表存在 1代表删除）';
//    comment on column  sys_tenant.create_dept        is '创建部门';
//    comment on column  sys_tenant.create_by          is '创建者';
//    comment on column  sys_tenant.create_time        is '创建时间';
//    comment on column  sys_tenant.update_by          is '更新者';
//    comment on column  sys_tenant.update_time        is '更新时间';
//     `org_address` varchar(500) DEFAULT '' COMMENT '位置',
//            `longitude` varchar(32) DEFAULT '' COMMENT '经度',
//            `latitude` varchar(32) DEFAULT '' COMMENT '纬度',
//    `contacts_name` varchar(32) DEFAULT '' COMMENT '联系人',
//            `region_id` varchar(32) DEFAULT '' COMMENT '区域',
//            `region_desc` varchar(500) DEFAULT '' COMMENT '详细地址',
//            `system_type` tinyint(4) DEFAULT '1' COMMENT '系统类型',
//            `status` tinyint(1) DEFAULT '1' COMMENT '管理服务器状态：',
//            `last_remind_time` bigint(20) DEFAULT '0' COMMENT '上次短信提醒时间',
//            `account_id` varchar(64) DEFAULT '' COMMENT '用户id',
//            `is_expire_login` tinyint(1) DEFAULT '0' COMMENT '机构过期后 是否可以登录 0 否 1 是',
//            `is_expire_alarm` tinyint(1) DEFAULT '1' COMMENT '守护服务到期后能否处警：1 处理 2 不处理',
//            `is_push_alarm` tinyint(1) DEFAULT '0' COMMENT '报警是否推送到子女APP，1推送，0不推送，默认不推送（仅对机构项目生效）',
//            `nb_host_sum` int(11) unsigned DEFAULT '0' COMMENT '项目支持的手持主机总数',
//            `org_operate_time` bigint(20) DEFAULT '0' COMMENT '项目运维时间',
//            `expire_time` bigint(20) DEFAULT '4102413839' COMMENT '机构项目有效期,-1默认永久',
//            `app_id` varchar(32) DEFAULT '' COMMENT '项目对接标识',
//            `app_secret` varchar(32) DEFAULT '' COMMENT '项目对接密钥',
//            `is_approved` tinyint(1) DEFAULT '1' COMMENT '是否审批 0 待审批 1已审批',
//            `org_title` varchar(255) DEFAULT '' COMMENT '机构网页软件标题',
//            `community_order_rate` decimal(5,2) DEFAULT '0.00' COMMENT '社区订单抽成比例',
//            `server_order_bonus` int(3) DEFAULT '0' COMMENT '服务订单分成百分比',
//            `franchise_fee` int(11) DEFAULT '0' COMMENT '平台金额',
//            `care_server_bonus` varchar(32) DEFAULT NULL COMMENT '守护服务分成比例，来邦:机构',
//            `dispose_time_daily` varchar(64) DEFAULT NULL COMMENT '来邦呼叫中心处警时间段（居家场景）',
//            `dispose_time_holiday` varchar(64) DEFAULT NULL COMMENT '节假日处警时间段',
//            `group_id` varchar(32) DEFAULT NULL COMMENT '所属集团id',
//            `is_data_communication` tinyint(1) DEFAULT '1' COMMENT '是否数据互通 1 是 0 否',
//            `is_use` tinyint(1) DEFAULT '1' COMMENT '是否启用，1启用，0不启用',
//            `back_color` tinyint(1) DEFAULT '1' COMMENT '背景颜色:0白色，1蓝色',
//            `manager_type` tinyint(1) DEFAULT '0' COMMENT '监管区域，0无，1养老院',
//            `last_pay_time` bigint(20) DEFAULT '0' COMMENT '社区项目最近一次交加盟费时间',
//            `is_expire_lb_alarm` tinyint(1) DEFAULT '1' COMMENT '守护服务到期后来邦是否参与处警：1 参与，2 不参与',
//            `care_expire_time` bigint(20) DEFAULT '0' COMMENT '守护服务到期时间',
//            `password_mode` tinyint(1) NOT NULL DEFAULT '1' COMMENT '密码校验模式（0 弱密码，1 强密码）',
//            `org_logo_url` varchar(255) DEFAULT '' COMMENT '标题图片地址',
//            `org_operate_fee_ratio` decimal(5,2) DEFAULT '0.10' COMMENT '运维费用比例',
//            `is_master_control_box` tinyint(1) DEFAULT '1' COMMENT '医院项目:是否使用主控盒 0否1是',
//            `jurisdiction_longitude` varchar(32) DEFAULT '' COMMENT '经度',
//            `jurisdiction_latitude` varchar(32) DEFAULT '' COMMENT '纬度',
//            `jurisdiction_address` varchar(255) DEFAULT NULL COMMENT '管辖区域地址',
//            `adcode` varchar(128) DEFAULT '' COMMENT 'gps对应高德adcode',
//            `customer_telephone` varchar(32) DEFAULT '' COMMENT '客服电话',
//            `org_alarm_push_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT
//            '机构场景报警推送类型（1-平台客服&居家客服&监护主机，2-平台客服&居家客服，3-平台客服&监护主机，4-居家客服&监护主机，5-平台客服，6-居家客服，7-监护主机）',
//            `lb_alarm_push_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT
//            '居家场景报警推送类型（1-平台客服&居家客服&监护主机，2-平台客服&居家客服，3-平台客服&监护主机，4-居家客服&监护主机，5-平台客服，6-居家客服，7-监护主机）',
//            `is_private` tinyint(1) DEFAULT '0' COMMENT '是否私有化,0否,1是',
//            `private_url` varchar(128) DEFAULT '' COMMENT '私有化地址',
//            `private_status` tinyint(1) DEFAULT '0' COMMENT '私有化状态,0关闭,1正常,2异常',
//            `private_error_times` tinyint(1) DEFAULT '0' COMMENT '私有化推送失败次数',
//            `change_password_cycle` int(11) NOT NULL DEFAULT '0' COMMENT '修改密码周期(单位：天)（0：无需修改）',
//            `login_fail_num` tinyint(1) NOT NULL DEFAULT '5' COMMENT '登录失败次数',
//            `login_limit_time` int(11) NOT NULL DEFAULT '300' COMMENT '登录限制时间(单位：秒)',
//            `is_white_center_customer_open` tinyint(1) NOT NULL DEFAULT '1' COMMENT '白名单是否开启来邦呼叫中心 0 不开启, 1 开启',
//            `subordinate_platform_num` int(11) DEFAULT '0' COMMENT '子平台数据',
//            `org_operate_fee` int(11) DEFAULT '0' COMMENT '平台金额',
//            `indoor_map_update_frequency` int(11) DEFAULT '0' COMMENT '室内地图更新频率',
//            `contain_system_type` varchar(16) DEFAULT '' COMMENT '包含平台:6机构养老2社区养老0居家养老3民政监管',
//            `call_center_name` varchar(128) DEFAULT '' COMMENT '呼叫中心名称',
//            `jurisdiction_coordsys` tinyint(1) DEFAULT '1' COMMENT '坐标系类型，0：WGS-84，1：百度，2：高德',
//            `coordsys` tinyint(1) DEFAULT '1' COMMENT '坐标系类型，0：WGS-84，1：百度，2：高德',
//            `is_shelter` tinyint(1) DEFAULT '0' COMMENT '是否为方舱，1是，0否',
//            `power_handle` tinyint(1) DEFAULT '0' COMMENT '低电量显示是否需要处理，1是0否',
//            `third_party_platform_type` tinyint(1) NOT NULL DEFAULT '-1' COMMENT '三方对接平台类型:-1不对接 0:数联天下',
//            `energy_monitor_end_time` bigint(20) DEFAULT '0' COMMENT '电量监控结束时间',
//            `platform_name` varchar(28) DEFAULT NULL COMMENT '登录页平台名称',
//            `platform_logo_url` varchar(128) DEFAULT NULL COMMENT '登录页图片',
//            `login_position` tinyint(1) DEFAULT '2' COMMENT '1居左 2居中 3居右 ，默认2',
//            `dispose_time_daily_org` varchar(64) DEFAULT NULL COMMENT '来邦呼叫中心处警时间段（机构场景）',
//            `end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '停用时间',
//            `customer_project_type` tinyint(1) DEFAULT '-1' COMMENT '客户项目类型:1客户试用项目2客户签单项目3客户私有化项目4客户对接项目9其他项目',
//            `is_abutment` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否开启监护设备对接功能0关闭1开启',
//            `app_customized` tinyint(1) DEFAULT '0' COMMENT 'APP是否定制，1是;0否',
//            `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除 0否 1是',
//            `nb_user_sum` int(11) NOT NULL DEFAULT '100' COMMENT '平台账户数量',
//            `creator` varchar(128) NOT NULL DEFAULT '' COMMENT '创建人',
//            `care_server_enable` tinyint(1) NOT NULL DEFAULT '0' COMMENT '呼叫中心守护服务是否开启0否1是',
//            `care_server_cost` int(11) NOT NULL DEFAULT '0' COMMENT '守护费用',
//            `vip_care_server_enable` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'vip守护服务是否开启0否1是',
//            `care_share_ratio` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '首单分成比例',
//            `withdraw_limit_type` tinyint(1) NOT NULL DEFAULT '2' COMMENT '提现限制1不限制2服务器到期才可以提现3自定义时间（天）',
//            `withdraw_limit_day` int(11) NOT NULL DEFAULT '0' COMMENT '提现限制天数',
//            `vip_basic_care_server_enable` tinyint(1) NOT NULL DEFAULT '0' COMMENT 'vip守护服务是否开启0否1是',
//            `is_participate_alarm` tinyint(1) DEFAULT '0' COMMENT '是否参与项目报警0否1是',
//            `share_model` tinyint(1) NOT NULL DEFAULT '0' COMMENT '分成模式0首单分成1首单分成+周期内分成',
//            `renew_share_cycle` int(11) NOT NULL DEFAULT '0' COMMENT '续费分成周期',
//            `renew_share_cycle_unit` tinyint(1) NOT NULL DEFAULT '1' COMMENT '续费分成周期单位1年2月3日',
//            `renew_share_ratio` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '续费分成比例',
//            `vip_dispose_time_daily` varchar(64) NOT NULL DEFAULT '' COMMENT 'VIP服务呼叫中心处警时间段',
//            `vip_basic_dispose_time_daily` varchar(64) NOT NULL DEFAULT '' COMMENT 'VIP服务呼叫中心处警时间段（基础版）',
//            `basic_share_model` tinyint(1) NOT NULL DEFAULT '0' COMMENT '分成模式0首单分成1首单分成+周期内分成（基础版）',
//            `basic_care_share_ratio` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '首单分成比例（基础版）',
//            `basic_renew_share_cycle` int(11) NOT NULL DEFAULT '0' COMMENT '续费分成周期（基础版）',
//            `basic_renew_share_cycle_unit` tinyint(1) NOT NULL DEFAULT '1' COMMENT '续费分成周期单位1年2月3日（基础版）',
//            `basic_renew_share_ratio` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '续费分成比例（基础版）',
//            `basic_withdraw_limit_type` tinyint(1) NOT NULL DEFAULT '2' COMMENT '提现限制1不限制2服务器到期才可以提现3自定义时间（天）(基础版)',
//            `basic_withdraw_limit_day` int(11) NOT NULL DEFAULT '0' COMMENT '提现限制天数（基础班）',
//            `nursing_push_config` tinyint(1) NOT NULL DEFAULT '0' COMMENT '机构护理服务推送设置 0-不推送 1-次日推送昨日护理服务',
//            `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认：0-否 1-是',
//            `is_init` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否初始：0-否 1-是',



