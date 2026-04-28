package com.lonbon.cloud.base.service;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface AttributeAvailable<T extends AttributeEntity> {

    @Nullable UUID getId();

    @Nullable List<T> getAttributes();
}
