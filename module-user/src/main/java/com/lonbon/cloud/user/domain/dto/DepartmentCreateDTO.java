package com.lonbon.cloud.user.domain.dto;

import com.lonbon.cloud.user.domain.entity.Department;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.UUID;

@Data
@AutoMapper(target = Department.class, reverseConvertGenerate = false)
public class DepartmentCreateDTO {

    @NotEmpty
    private String type;

    @NotEmpty
    private String name;

    private String description;

    private Boolean isDefault;

    private Boolean isActive;

    private Integer sort_order;

    private String phone;

    private UUID leaderStaffId;

    private String officeLocation;

    private UUID parentId;

}
