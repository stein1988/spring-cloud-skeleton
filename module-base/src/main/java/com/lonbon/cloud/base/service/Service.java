package com.lonbon.cloud.base.service;

import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public interface Service<T> {
    T createEntity(Object createDto);
    T updateEntity(UUID id, Object updateDto);
    T updateEntity(UUID id, Function<T, T> updateFunc);
    void deleteEntity(UUID id);
    Optional<T> getEntityById(UUID id);
//    Optional<T> getEntityByName(String name);
    List<T> getAllEntities();
    PageResult<T> getPaginationEntities(Object whereObject, Pageable pageable);
}
