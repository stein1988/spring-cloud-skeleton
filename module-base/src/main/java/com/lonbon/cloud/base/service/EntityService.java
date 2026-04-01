package com.lonbon.cloud.base.service;

import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;
import io.github.linpeilie.Converter;

import java.util.Optional;

public class EntityService<T, ID> implements Service<T, ID> {

    protected Converter converter;

    @Override
    public T createEntity(Object createDto) {
        return null;
    }

    @Override
    public T updateEntity(ID id, Object updateDto) {
        return null;
    }

    @Override
    public void deleteEntity(ID id) {

    }

    @Override
    public Optional<T> getEntityByTd(ID id) {
        return Optional.empty();
    }

    @Override
    public Iterable<T> getAllEntities() {
        return null;
    }

    @Override
    public PageResult<T> getPaginationEntities(Object whereObject, Pageable pageable) {
        return null;
    }
}
