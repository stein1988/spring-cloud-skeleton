package com.lonbon.cloud.user.domain.dto;

import com.lonbon.cloud.user.domain.entity.Role;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@AutoMapper(target = Role.class)
@Data
public class RoleCreateDTO {
    @NotNull
    private UUID tenantId;
    
    @NotNull
    private UUID teamId;
    
    private String type;
    
    @NotEmpty
    @NotNull
    private String name;
    
    private String description;
}
