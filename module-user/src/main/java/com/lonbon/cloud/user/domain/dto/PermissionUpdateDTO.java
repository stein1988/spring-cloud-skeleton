package com.lonbon.cloud.user.domain.dto;

import com.lonbon.cloud.user.domain.entity.Permission;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

@AutoMapper(target = Permission.class)
@Data
public class PermissionUpdateDTO {
    private String type;
    private String name;
    private String description;
    private String resource;
    private String action;
}
