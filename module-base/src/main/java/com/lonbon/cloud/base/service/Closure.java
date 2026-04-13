package com.lonbon.cloud.base.service;

import java.util.UUID;

public interface Closure {
    UUID getAncestorId();

    UUID getDescendantId();

    int getDistance();
}
