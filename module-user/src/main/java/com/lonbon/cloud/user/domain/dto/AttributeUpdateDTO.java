package com.lonbon.cloud.user.domain.dto;

import com.lonbon.cloud.user.domain.entity.StaffAttribute;
import com.lonbon.cloud.user.domain.entity.TenantAttribute;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.UUID;

@Data
@AutoMappers({@AutoMapper(target = TenantAttribute.class, reverseConvertGenerate = false), @AutoMapper(target =
        StaffAttribute.class, reverseConvertGenerate = false)})
public class AttributeUpdateDTO {

    private UUID id;

    @NotEmpty
    private String key;

    private String value;
}
