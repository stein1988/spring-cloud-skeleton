package com.lonbon.cloud.base.service;

import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;

import java.util.Optional;

public interface Service<T, ID> {
    T createEntity(Object createDto);
    T updateEntity(ID id, Object updateDto);
    void deleteEntity(ID id);
    Optional<T> getEntityByTd(ID id);
//    Optional<T> getEntityByName(String name);
    Iterable<T> getAllEntities();
    PageResult<T> getPaginationEntities(Object whereObject, Pageable pageable);
}
