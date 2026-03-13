package com.lonbon.cloud.base.entity;

import com.easy.query.core.basic.jdbc.types.handler.JdbcTypeHandler;
import org.jetbrains.annotations.NotNull;

public interface JdbcTypeHandlerConfigurer extends JdbcTypeHandler {

    @NotNull
    Class<?>  getType();
}
