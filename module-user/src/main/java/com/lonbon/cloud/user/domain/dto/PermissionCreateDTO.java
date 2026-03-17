package com.lonbon.cloud.user.domain.dto;

import com.lonbon.cloud.user.domain.entity.Permission;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@AutoMapper(target = Permission.class)
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
