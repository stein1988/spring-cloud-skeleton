package com.lonbon.cloud.base.entity;

import com.easy.query.core.basic.jdbc.executor.internal.merge.result.StreamResultSet;
import com.easy.query.core.basic.jdbc.executor.internal.props.JdbcProperty;
import com.easy.query.core.basic.jdbc.types.EasyParameter;
import com.easy.query.core.basic.jdbc.types.handler.JdbcTypeHandler;
import com.easy.query.sql.starter.config.JdbcTypeHandlerReplaceConfigurer;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;

/**
 * PostgreSQL timestamptz 类型与 Java OffsetDateTime 类型的处理器
 * <p>
 * 负责处理PostgreSQL的TIMESTAMP WITH TIME ZONE类型与Java的OffsetDateTime类型之间的转换。
 * 所有时间戳均以UTC时区存储和读取，确保跨时区数据一致性。
 * </p>
 *
 * @author lonbon
 * @since 1.0.0
 * @todo 其他类型数据库的带时区时间戳字段测试
 */
@Component
public class OffsetDateTimeTypeHandler implements JdbcTypeHandler, JdbcTypeHandlerReplaceConfigurer {

    /**
     * 是否替换默认的类型处理器
     *
     * @return true-替换默认的OffsetDateTime类型处理器
     */
    @Override
    public boolean replace() {
        return true;
    }

    /**
     * 返回允许处理的类型集合
     *
     * @return 包含OffsetDateTime类型的集合
     */
    @Override
    public Set<Class<?>> allowTypes() {
        return Set.of(OffsetDateTime.class);
    }

    /**
     * 从结果集中读取OffsetDateTime值
     * <p>
     * 将数据库的Timestamp类型转换为Java的OffsetDateTime类型。
     * 使用UTC时区确保时间一致性。
     * </p>
     *
     * @param jdbcProperty    JDBC属性
     * @param streamResultSet 结果集
     * @return OffsetDateTime值，如果数据库值为NULL则返回null
     * @throws SQLException 如果读取失败
     */
    @Override
    public @Nullable Object getValue(JdbcProperty jdbcProperty, StreamResultSet streamResultSet) throws SQLException {
        // 从结果集中读取Timestamp
        Timestamp timestamp = streamResultSet.getTimestamp(jdbcProperty.getJdbcIndex());
        if (timestamp == null) {
            return null;
        }
        // 将Timestamp转换为OffsetDateTime，使用UTC时区
        return timestamp.toInstant().atOffset(ZoneOffset.UTC);
    }

    /**
     * 设置OffsetDateTime参数
     * <p>
     * 将Java的OffsetDateTime类型转换为数据库的Timestamp类型。
     * 处理NULL值时使用Types.TIMESTAMP_WITH_TIMEZONE类型标识。
     * </p>
     *
     * @param parameter SQL参数
     * @throws SQLException 如果设置失败
     */
    @Override
    public void setParameter(EasyParameter parameter) throws SQLException {
        OffsetDateTime value = (OffsetDateTime) parameter.getValue();
        if (value == null) {
            // NULL值设置为TIMESTAMP_WITH_TIMEZONE类型
            parameter.getPs().setNull(parameter.getIndex(), Types.TIMESTAMP_WITH_TIMEZONE);
        } else {
            // 将 OffsetDateTime 转换为 Timestamp
            Timestamp timestamp = Timestamp.from(value.toInstant());
            parameter.getPs().setTimestamp(parameter.getIndex(), timestamp);
        }
    }
}
