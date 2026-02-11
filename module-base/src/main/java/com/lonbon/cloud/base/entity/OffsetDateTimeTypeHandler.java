package com.lonbon.cloud.base.entity;

import com.easy.query.core.basic.jdbc.executor.internal.merge.result.StreamResultSet;
import com.easy.query.core.basic.jdbc.executor.internal.props.JdbcProperty;
import com.easy.query.core.basic.jdbc.types.EasyParameter;
import com.easy.query.core.basic.jdbc.types.handler.JdbcTypeHandler;
import com.easy.query.sql.starter.config.JdbcTypeHandlerReplaceConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

/**
 * PostgreSQL timestamptz 类型与 Java OffsetDateTime 类型的处理器
 * TODO：其他类型数据库的带时区时间戳字段测试
 */
@Slf4j
@Component
public class OffsetDateTimeTypeHandler implements JdbcTypeHandler, JdbcTypeHandlerReplaceConfigurer {

    public static final OffsetDateTimeTypeHandler INSTANCE = new OffsetDateTimeTypeHandler();

    @Override
    public boolean replace() {
        return true;
    }

    @Override
    public Set<Class<?>> allowTypes() {
        Set<Class<?>> types = new HashSet<>();
        types.add(OffsetDateTime.class);
        return types;
    }

    @Override
    public Object getValue(JdbcProperty jdbcProperty, StreamResultSet streamResultSet) throws SQLException {
        Timestamp timestamp = streamResultSet.getTimestamp(jdbcProperty.getJdbcIndex());
        if (timestamp == null) {
            return null;
        }
        // 将 Timestamp 转换为 OffsetDateTime
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        log.info("localDateTime={}", localDateTime);
        OffsetDateTime offsetDateTime = timestamp.toInstant().atOffset(ZoneOffset.UTC);
        log.info("offsetDateTime={}", offsetDateTime);

        return offsetDateTime;
    }

    @Override
    public void setParameter(EasyParameter parameter) throws SQLException {
        OffsetDateTime value = (OffsetDateTime) parameter.getValue();
        if (value == null) {
            parameter.getPs().setNull(parameter.getIndex(), Types.TIMESTAMP_WITH_TIMEZONE);
        } else {
            // 将 OffsetDateTime 转换为 Timestamp
            Timestamp timestamp = Timestamp.from(value.toInstant());
            parameter.getPs().setTimestamp(parameter.getIndex(), timestamp);
        }
    }
}
