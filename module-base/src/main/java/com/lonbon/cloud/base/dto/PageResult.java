package com.lonbon.cloud.base.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页结果
 * <p>
 * 用于封装分页查询的结果，包含分页信息和数据列表。
 * </p>
 *
 * @param <T> 数据项类型
 * @author lonbon
 * @since 1.0.0
 */
@Data
public class PageResult<T> {
    
    /**
     * 当前页码（从1开始）
     */
    private int page;
    
    /**
     * 每页条数
     */
    private int size;
    
    /**
     * 总记录数
     */
    private long total;
    
    /**
     * 数据列表
     */
    private List<T> items;

    /**
     * 构造分页结果
     *
     * @param pageable 分页参数
     * @param total    总记录数
     * @param items    数据列表
     */
    public PageResult(Pageable pageable, long total, List<T> items) {
        this.page = pageable.getPage();
        this.size = pageable.getSize();
        this.total = total;
        this.items = items;
    }
}
