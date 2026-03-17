package com.lonbon.cloud.base.dto;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private int page;
    private int size;
    private long total;
    private List<T> items;

    public PageResult(Pageable pageable, long total, List<T> items) {
        this.page = pageable.getPage();
        this.size = pageable.getSize();
        this.total = total;
        this.items = items;
    }
}
