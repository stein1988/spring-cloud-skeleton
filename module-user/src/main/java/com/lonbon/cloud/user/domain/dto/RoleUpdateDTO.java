package com.lonbon.cloud.user.domain.dto;

import com.lonbon.cloud.user.domain.entity.Role;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.util.UUID;

@AutoMapper(target = Role.class)
@Data
public class RoleUpdateDTO {
    private String type;
    private String name;
    private String description;
}
