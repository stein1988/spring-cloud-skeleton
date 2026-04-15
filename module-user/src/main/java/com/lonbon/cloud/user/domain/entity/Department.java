package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.DepartmentProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

/**
 * 部门
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_department", ignoreProperties = {"departmentId"})
@EntityProxy
public class Department extends BaseEntity implements ProxyEntityAvailable<Department, DepartmentProxy> {

    /**
     * 类型
     * 对应lb_location_care.lb_ims_department.department_type
     * TODO：确定字典表的意义
     */
    private String type;

    /**
     * 名称
     * 对应lb_location_care.lb_ims_department.department_name
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否默认部门
     * 对应lb_location_care.lb_ims_department.is_default
     */
    private Boolean isDefault;

    /**
     * 是否激活
     */
    @Column(dbDefault = "true")
    private Boolean isActive;

    /**
     * 显示顺序
     * 对应lb_location_care.lb_ims_department.order_num
     */
    private Integer sort_order;

    /**
     * 电话号码
     * 对应lb_location_care.lb_ims_department.phone_num
     */
    private String phone;

    /**
     * 部门领导staffId
     * TODO：研究leaderStaffName能不能导航出来
     */
    private UUID leaderStaffId;

    /**
     * 办公位置
     * 对应lb_location_care.lb_ims_department.office_location
     */
    private String OfficeLocation;


}


/*
create table if not exists rouyi_cloud_plus.sys_dept
(
  dept_id     int8,
  tenant_id   varchar(20) default '000000'::varchar,
  parent_id   int8        default 0,
  ancestors   varchar(500)default ''::varchar,
  dept_name   varchar(30) default ''::varchar,
  dept_category varchar(100) default null::varchar,
  order_num   int4        default 0,
  leader      int8        default null,
  phone       varchar(11) default null::varchar,
  email       varchar(50) default null::varchar,
  status      char        default '0'::bpchar,
  del_flag    char        default '0'::bpchar,
  create_dept int8,
  create_by   int8,
  create_time timestamp,
  update_by   int8,
  update_time timestamp,
  constraint "sys_dept_pk" primary key (dept_id)
);

comment on table sys_dept               is '部门表';
comment on column sys_dept.dept_id      is '部门ID';
comment on column sys_dept.tenant_id    is '租户编号';
comment on column sys_dept.parent_id    is '父部门ID';
comment on column sys_dept.ancestors    is '祖级列表';
comment on column sys_dept.dept_name    is '部门名称';
comment on column sys_dept.dept_category    is '部门类别编码';
comment on column sys_dept.order_num    is '显示顺序';
comment on column sys_dept.leader       is '负责人';
comment on column sys_dept.phone        is '联系电话';
comment on column sys_dept.email        is '邮箱';
comment on column sys_dept.status       is '部门状态（0正常 1停用）';
comment on column sys_dept.del_flag     is '删除标志（0代表存在 1代表删除）';
comment on column sys_dept.create_dept  is '创建部门';
comment on column sys_dept.create_by    is '创建者';
comment on column sys_dept.create_time  is '创建时间';
comment on column sys_dept.update_by    is '更新者';
comment on column sys_dept.update_time  is '更新时间';

CREATE TABLE `lb_location_care.lb_ims_department` (
  `department_id` varchar(32) NOT NULL COMMENT '主键',
  `org_id` varchar(32) NOT NULL DEFAULT '' COMMENT '机构主键',
  `department_name` varchar(32) NOT NULL DEFAULT '' COMMENT '部门名称',
  `department_head` varchar(32) DEFAULT '' COMMENT '部门领导',
  `office_location` varchar(32) DEFAULT '' COMMENT '办公位置',
  `phone_num` varchar(11) DEFAULT '' COMMENT '手机号码',
  `superior_department` varchar(32) DEFAULT '0' COMMENT '上级部门',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除（0 未删除，1 已删除）',
  `create_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '创建时间',
  `update_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '更新时间',
  `department_type` varchar(32) DEFAULT NULL COMMENT '部门类型:0其他部门 1综合管理部 2财务管理部 3安全保障部 4后勤管理部 5护理管理部 6医务管理部 7人事管理部
  8管家管理部',
  TODO：`in_care_notice` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否接收长者入住通知(0:否  1:是)',
  `order_num` int(11) NOT NULL DEFAULT '1' COMMENT '序号',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认：0-否 1-是',
  `is_init` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否初始：0-否 1-是',
  PRIMARY KEY (`department_id`) USING BTREE,
  KEY `org_id` (`org_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='部门表';



 */