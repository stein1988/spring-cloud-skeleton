package com.lonbon.cloud.user.domain.dto.tenant;


import com.lonbon.cloud.user.domain.dto.AttributeCreateDTO;
import com.lonbon.cloud.user.domain.entity.Tenant;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@AutoMapper(target = Tenant.class, reverseConvertGenerate = false)
@Data
public class CreateDTO {


    /**
     * 类型
     * <p>
     * 对应 lb_location_care.lb_organization.org_type
     * </p>
     *
     * @todo 确定字典表的意义
     */
    private String type;
    /**
     * 名称
     * <p>
     * 对应 lb_location_care.lb_organization.org_name
     * </p>
     */
    private String name;
    /**
     * 描述
     * <p>
     * 对应 lb_location_care.lb_organization.org_desc
     * </p>
     */
    private String description;
    /**
     * 父节点ID
     * <p>
     * 用于构建树形结构，null表示根节点
     * </p>
     */
    private UUID parentId;


    private List<AttributeCreateDTO> attributes;
}
