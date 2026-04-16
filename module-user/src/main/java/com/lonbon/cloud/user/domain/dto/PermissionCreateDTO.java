package com.lonbon.cloud.user.domain.dto;

import com.lonbon.cloud.user.domain.entity.Permission;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@AutoMapper(target = Permission.class, reverseConvertGenerate = false)
@Data
public class PermissionCreateDTO {
    @NotNull
    private UUID tenantId;

    @NotNull
    private UUID teamId;

    private String type;

    @NotEmpty
    @NotNull
    private String name;

    private String description;

    @NotEmpty
    @NotNull
    private String resource;

    @NotEmpty
    @NotNull
    private String action;
}
