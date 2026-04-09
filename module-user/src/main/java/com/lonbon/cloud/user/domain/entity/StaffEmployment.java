package com.lonbon.cloud.user.domain.entity;

import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import com.lonbon.cloud.base.entity.BaseEntity;
import com.lonbon.cloud.user.domain.entity.proxy.StaffEmploymentProxy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 员工雇佣与合同信息表
 * 对应旧表：lb_location_care.lb_ims_staff（雇佣合同字段）
 * 职责：职务、岗位、合同、薪资等HR业务信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "sys_staff_employment")
@EntityProxy
public class StaffEmployment extends BaseEntity implements ProxyEntityAvailable<StaffEmployment, StaffEmploymentProxy> {

    /* 已继承BaseEntity字段：
       id、tenantId、createTime、updateTime、isDeleted
     */

    /**
     * 员工ID（关联sys_staff.id）
     */
    private Long staffId;

    /**
     * 职务
     * 对应旧表：lb_ims_staff.office
     */
    private String office;

    /**
     * 工作岗位
     * 对应旧表：lb_ims_staff.job_position
     */
    private String jobPosition;

    /**
     * 职称
     * 对应旧表：lb_ims_staff.job_title
     */
    private String jobTitle;

    /**
     * 职称级别编码
     * 对应旧表：lb_ims_staff.job_title_level
     * TODO：关联字典表 sys_job_title_level
     */
    private int jobTitleLevel;

    /**
     * 人员类型编码
     * 对应旧表：lb_ims_staff.personnel_type
     * TODO：关联字典表 sys_personnel_type
     */
    private int personnelType;

    /**
     * 合同类型编码
     * 对应旧表：lb_ims_staff.contract_type
     * TODO：关联字典表 sys_contract_type
     */
    private int contractType;

    /**
     * 合同编号
     * 对应旧表：lb_ims_staff.contract_num
     */
    private String contractNum;

    /**
     * 合同开始日期
     * 对应旧表：lb_ims_staff.contract_start_time
     */
    private LocalDate contractStartDate;

    /**
     * 合同结束日期
     * 对应旧表：lb_ims_staff.contract_end_update_time
     */
    private LocalDate contractEndDate;

    /**
     * 合同附件URL
     * 对应旧表：lb_ims_staff.contract_attachment
     */
    private String contractAttachment;

    /**
     * 合同附件名称
     * 对应旧表：lb_ims_staff.contract_attachment_name
     */
    private String contractAttachmentName;

    /**
     * 薪金（元）
     * 对应旧表：lb_ims_staff.salary
     * TODO：国密SM4加密存储，仅HR角色可访问
     */
    private BigDecimal salary;

    /**
     * 其他待遇
     * 对应旧表：lb_ims_staff.other_treatment
     */
    private String otherTreatment;

    /**
     * 金蝶部门编码
     * 对应旧表：lb_ims_staff.kingdee_expense_department
     */
    private String kingdeeExpenseDepartment;
}
