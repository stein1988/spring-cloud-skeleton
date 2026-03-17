package com.lonbon.cloud.base.dto;

import lombok.Data;

@Data
public class Sortable {
    // 排序方向
    public enum Direction {
        ASC, DESC
    }

    // 排序字段（实体字段名）
    private String property;
    // 排序方向（默认 DESC）
    private Direction direction = Direction.DESC;

    public Sortable(String property, Direction direction) {
        this.property = property;
        this.direction = direction;
    }
}
