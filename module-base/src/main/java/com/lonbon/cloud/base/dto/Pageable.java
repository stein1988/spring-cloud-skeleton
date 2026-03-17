package com.lonbon.cloud.base.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;

import java.util.ArrayList;
import java.util.List;

@Data
@ParameterObject
@Schema(description = "自定义分页参数")
public class Pageable {

    @Schema(description = "页码（从0开始）", example = "0", defaultValue = "0")
    private Integer page;

    @Schema(description = "每页条数", example = "10", defaultValue = "10")
    private Integer size;

    // Spring MVC 对 List 类型的请求参数，会以 逗号、空格、分号 作为分隔符，将参数值拆分成多个元素。
    // 例如 sort=createTime,desc 会被拆分为 ["createTime", "desc"]。
    // 所以改用点号做分隔符
    @Schema(description = "排序规则，格式：字段名.方向（asc/desc），可以接受多个参数，注意分隔符是点，不是逗号",
            example = "createdAt.desc")
    private List<String> sort;

    @Schema(description = "解析后的排序数据，不显示在接口文档中", hidden = true)
    @Setter(AccessLevel.NONE)   // 内部缓存字段，禁用Setter
    private List<Sortable> sortables;

    public @NotNull Integer getPage() {
        if (page == null || page < 0)
            page = 0;

        return page;
    }

    public @NotNull Integer getSize() {
        if (size == null || size <= 0)
            size = 10;

        return size;
    }

    public @NotNull List<Sortable> getSortables() {
        if (this.sortables != null)
            return this.sortables;

        this.sortables = new ArrayList<>();
        if (this.sort == null)
            return this.sortables;

        for (String s : this.sort) {
            if (StringUtils.isBlank(s))
                continue;

            // 拆分 字段,方向
            String[] parts = s.split("\\.");
            String property = parts[0].trim();
            if (StringUtils.isBlank(property)) {
                continue;
            }
            // 解析排序方向
            Sortable.Direction direction = Sortable.Direction.DESC;
            if (parts.length >= 2) {
                String dirStr = parts[1].trim().toUpperCase();
                try {
                    direction = Sortable.Direction.valueOf(dirStr);
                } catch (IllegalArgumentException _) {
                    
                }
            }

            this.sortables.add(new Sortable(property, direction));
        }

        return this.sortables;
    }

    public boolean hasSort() {
        return !this.getSortables().isEmpty();
    }

    public long getOffset() {
        return (long) getPage() * getSize();
    }
}
