package com.lonbon.cloud.base.service;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface ClosureAvailable<T extends Closure> {

    UUID getId();

    @Nullable UUID getParentId();

    void setParentId(UUID parentId);

    @Nullable List<T> getAncestors();
}
