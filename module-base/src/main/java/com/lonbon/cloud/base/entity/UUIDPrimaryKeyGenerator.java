package com.lonbon.cloud.base.entity;

import com.easy.query.core.basic.extension.generated.PrimaryKeyGenerator;
import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * UUID主键生成器
 * <p>
 * 使用 UUIDv7（时间有序UUID）算法生成主键。
 * UUIDv7 具有时间有序性，查询性能优于随机UUID。
 * </p>
 *
 * @author lonbon
 * @since 1.0.0
 * @see <a href="https://www.easy-query.com/easy-query-doc/adv/key-generator.html">EasyQuery主键生成器文档</a>
 */
@Component
public class UUIDPrimaryKeyGenerator implements PrimaryKeyGenerator {
    
    /**
     * 生成UUIDv7主键
     * <p>
     * 使用时间有序UUID算法，确保新生成的ID在时间上总是大于旧的ID。
     * 这对于数据库索引性能非常有利。
     * </p>
     *
     * @return 生成的UUID
     */
    @Override
    public Serializable getPrimaryKey() {
        return UuidCreator.getTimeOrderedEpoch();
    }

//  默认情况下，不重载 setPrimaryKey 函数，那么主键会被强制设置为 getPrimaryKey 的返回值
//  如果要进行其他逻辑判断，比如主键不为空时，再生成主键，可以参考下面的代码
//    @Override
//    public void setPrimaryKey(Object entity, ColumnMetadata columnMetadata) {
//        Object oldValue = columnMetadata.getGetterCaller().apply(entity);
//        if(oldValue == null)
//        {
//           PrimaryKeyGenerator.super.setPrimaryKey(entity,columnMetadata);
//        }
//    }
}
