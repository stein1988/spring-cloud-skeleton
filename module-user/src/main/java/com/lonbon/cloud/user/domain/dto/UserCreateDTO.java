package com.lonbon.cloud.user.domain.dto;

import com.lonbon.cloud.user.domain.entity.User;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@AutoMapper(target = User.class)
@Data
public class UserCreateDTO {

    @NotEmpty
    @NotNull
    private String username;
    
    @NotEmpty
    @NotNull
    private String passwordHash;
    
    @NotEmpty
    @NotNull
    private String passwordSalt;
    
    @NotNull
    private UUID currentTenantId;
    
    @NotNull
    private UUID currentTeamId;
    
    private boolean isSuperAdmin;
    private boolean isActive;
    private String name;
    private String email;
    private String phone;
    private String avatarUrl;
    private String timezone;
    private String language;
}
