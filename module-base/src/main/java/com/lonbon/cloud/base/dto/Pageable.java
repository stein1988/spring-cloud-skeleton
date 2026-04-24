package com.lonbon.cloud.base.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;
import org.springdoc.core.annotations.ParameterObject;

import java.util.ArrayList;
import java.util.List;

@Data
@ParameterObject
@Schema(description = "自定义分页参数")
public class Pageable {

    @Schema(description = "页码（从1开始）", example = "1", defaultValue = "1")
    private @Nullable Integer page;

    @Schema(description = "每页条数", example = "10", defaultValue = "10")
    private @Nullable Integer size;

    // Spring MVC 对 List 类型的请求参数，会以 逗号、空格、分号 作为分隔符，将参数值拆分成多个元素。
    // 例如 sort=createTime,desc 会被拆分为 ["createTime", "desc"]。
    // 所以改用点号做分隔符
    @Schema(description = "排序规则，格式：字段名.方向（asc/desc），可以接受多个参数，例如：user.name.desc 或 user.department.name.asc，当不指定方向时默认 " +
            "asc", example = "id.desc")
    private @Nullable List<String> sort;

    @Schema(description = "解析后的排序数据，不显示在接口文档中", hidden = true)
    @Setter(AccessLevel.NONE)   // 内部缓存字段，禁用Setter
    private @Nullable List<Sortable> sortables;

    public Integer getPage() {
        if (page == null || page <= 0) page = 1;

        return page;
    }

    public Integer getSize() {
        if (size == null || size <= 0) size = 10;

        return size;
    }

    public List<Sortable> getSortables() {
        if (this.sortables != null) return this.sortables;

        this.sortables = new ArrayList<>();
        if (this.sort == null) return this.sortables;

        for (String s : this.sort) {
            if (StringUtils.isBlank(s)) continue;

            String trimmed = s.trim();
            if (StringUtils.isBlank(trimmed)) continue;

            // 提取末尾的方向（asc 或 desc，大小写忽略）
            String[] parts = trimmed.split("\\.");
            String lastPart = parts[parts.length - 1].trim().toLowerCase();
            Sortable.Direction direction = Sortable.Direction.ASC;

            if (lastPart.equals("asc") || lastPart.equals("desc")) {
                // 去掉末尾的方向部分，其余都是字段名
                StringBuilder propertyBuilder = new StringBuilder();
                for (int i = 0; i < parts.length - 1; i++) {
                    if (i > 0) propertyBuilder.append(".");
                    propertyBuilder.append(parts[i].trim());
                }
                direction = lastPart.equals("desc") ? Sortable.Direction.DESC : Sortable.Direction.ASC;
                String property = propertyBuilder.toString();
                if (StringUtils.isNotBlank(property)) {
                    this.sortables.add(new Sortable(property, direction));
                }
            } else {
                // 没有指定方向，全部都是字段名，默认 asc
                String property = trimmed;
                if (StringUtils.isNotBlank(property)) {
                    this.sortables.add(new Sortable(property, direction));
                }
            }
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
