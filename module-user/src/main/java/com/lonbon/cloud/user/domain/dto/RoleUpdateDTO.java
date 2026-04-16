package com.lonbon.cloud.user.domain.dto;

import com.lonbon.cloud.user.domain.entity.Role;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

@AutoMapper(target = Role.class, reverseConvertGenerate = false)
@Data
public class RoleUpdateDTO {
    private String type;
    private String name;
    private String description;
}
