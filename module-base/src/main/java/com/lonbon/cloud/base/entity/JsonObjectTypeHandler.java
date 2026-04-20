package com.lonbon.cloud.base.entity;

import com.easy.query.core.basic.jdbc.executor.internal.merge.result.StreamResultSet;
import com.easy.query.core.basic.jdbc.executor.internal.props.JdbcProperty;
import com.easy.query.core.basic.jdbc.parameter.SQLParameter;
import com.easy.query.core.basic.jdbc.types.EasyParameter;
import com.easy.query.core.basic.jdbc.types.handler.JdbcTypeHandler;
import com.easy.query.sql.starter.config.JdbcTypeHandlerReplaceConfigurer;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Component;

import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.Set;

/**
 * JSON对象类型处理器
 * <p>
 * 负责处理实现了 {@link JsonObject} 接口的类型与数据库JSONB类型之间的转换。
 * 支持从数据库读取JSON字符串和将JSON字符串写入PostgreSQL的JSONB类型。
 * </p>
 *
 * @author lonbon
 * @since 1.0.0
 */
@Component
public class JsonObjectTypeHandler implements JdbcTypeHandler, JdbcTypeHandlerReplaceConfigurer {

    /**
     * 是否替换默认的类型处理器
     *
     * @return true-替换默认的String类型处理器
     */
    @Override
    public boolean replace() {
        return true;
    }

    /**
     * 返回允许处理的类型集合
     *
     * @return 包含String类型的集合
     */
    @Override
    public Set<Class<?>> allowTypes() {
        return Set.of(String.class);
    }

    /**
     * 从结果集中读取值
     * <p>
     * 直接返回字符串类型的值，JSON解析由应用层处理。
     * </p>
     *
     * @param jdbcProperty    JDBC属性
     * @param streamResultSet 结果集
     * @return 字符串值
     * @throws SQLException 如果读取失败
     */
    @Override
    public Object getValue(JdbcProperty jdbcProperty, StreamResultSet streamResultSet) throws SQLException {
        return streamResultSet.getString(jdbcProperty.getJdbcIndex());
    }

    /**
     * 设置SQL参数
     * <p>
     * 处理JsonObject类型字段的参数设置，将其转换为PostgreSQL的JSONB类型。
     * 当检测到JDBC类型为OTHER且字段实现了JsonObject接口时，
     * 创建PGobject设置为jsonb类型。
     * </p>
     *
     * @param parameter SQL参数
     * @throws SQLException 如果设置参数失败
     */
    @Override
    public void setParameter(EasyParameter parameter) throws SQLException {
        SQLParameter sqlParameter = parameter.getSQLParameter();
        JDBCType jdbcType = sqlParameter.getJdbcType();
        // 检测JDBC类型是否为OTHER，可能是JSON/JSONB类型
        if (jdbcType == JDBCType.OTHER) {
            Class<?> propertyType = sqlParameter.getColumnMetadata().getPropertyType();
            // 判断是否为JsonObject类型或其子类
            if (JsonObject.class.isAssignableFrom(propertyType)) {
                // TODO：还要判断当前的数据库类型做特殊处理
                // 创建PostgreSQL的JSONB对象
                PGobject pGobject = new PGobject();
                pGobject.setType("jsonb");
                pGobject.setValue((String) parameter.getValue());
                parameter.getPs().setObject(parameter.getIndex(), pGobject);
                return;
            }
        }

        // 非JsonObject类型，使用普通的字符串设置
        parameter.getPs().setString(parameter.getIndex(), (String)parameter.getValue());
    }
}
