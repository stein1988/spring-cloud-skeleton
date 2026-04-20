package com.lonbon.cloud.user.domain.dto;

import com.lonbon.cloud.user.domain.entity.Permission;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@AutoMapper(target = Permission.class, reverseConvertGenerate = false)
@Data
public class PermissionCreateDTO {
    private String type;

    @NotEmpty
    @NotNull
    private String name;

    private String description;

    @NotEmpty
    private String code;

    private String resource;

    private String action;
}
