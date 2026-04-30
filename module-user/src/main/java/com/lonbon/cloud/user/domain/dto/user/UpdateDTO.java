package com.lonbon.cloud.user.domain.dto.user;

import com.lonbon.cloud.user.domain.entity.User;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.util.UUID;

@AutoMapper(target = User.class, reverseConvertGenerate = false)
@Data
public class UpdateDTO {
    private String passwordHash;
    private String passwordSalt;
    private UUID currentTenantId;
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
