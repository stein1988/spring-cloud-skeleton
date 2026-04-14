package com.lonbon.cloud.base.service;

import java.util.List;
import java.util.UUID;

public interface ClosureAvailable<T extends Closure> {

    UUID getId();

    UUID getParentId();

    void setParentId(UUID parentId);

    List<T> getAncestors();
}
