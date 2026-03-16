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

@Component
public class JsonObjectTypeHandler implements JdbcTypeHandler, JdbcTypeHandlerReplaceConfigurer {

    @Override
    public boolean replace() {
        return true;
    }

    @Override
    public Set<Class<?>> allowTypes() {
        return Set.of(String.class);
    }

    @Override
    public Object getValue(JdbcProperty jdbcProperty, StreamResultSet streamResultSet) throws SQLException {
        return streamResultSet.getString(jdbcProperty.getJdbcIndex());
    }

    @Override
    public void setParameter(EasyParameter parameter) throws SQLException {
        SQLParameter sqlParameter = parameter.getSQLParameter();
        JDBCType jdbcType = sqlParameter.getJdbcType();
        if (jdbcType == JDBCType.OTHER) {
            Class<?> propertyType = sqlParameter.getColumnMetadata().getPropertyType();
            if (JsonObject.class.isAssignableFrom(propertyType)) {
                // TODO：还要判断当前的数据库类型做特殊处理
                PGobject pGobject = new PGobject();
                pGobject.setType("jsonb");
                pGobject.setValue((String) parameter.getValue());
                parameter.getPs().setObject(parameter.getIndex(), pGobject);
                return;
            }
        }

        parameter.getPs().setString(parameter.getIndex(), (String)parameter.getValue());
    }
}
