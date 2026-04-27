package com.lonbon.cloud.base.service;

import java.util.List;
import java.util.UUID;

public interface AttributeAvailable<T extends AttributeEntity> {

    UUID getId();

    List<T> getAttributes();
}
