package com.lonbon.cloud.base.repository;

import com.easy.query.core.api.dynamic.sort.ObjectSort;
import com.easy.query.core.api.dynamic.sort.ObjectSortBuilder;
import com.lonbon.cloud.base.dto.Sortable;

import java.util.List;

public class EasyQuerySort implements ObjectSort {

    private final List<Sortable> sortables;

    public EasyQuerySort(List<Sortable> sortables) {
        this.sortables = sortables;
    }

    @Override
    public void configure(ObjectSortBuilder builder) {
        for (Sortable sortable : sortables) {
            builder.orderBy(sortable.getProperty(), sortable.getDirection() == Sortable.Direction.ASC);
        }
    }
}
