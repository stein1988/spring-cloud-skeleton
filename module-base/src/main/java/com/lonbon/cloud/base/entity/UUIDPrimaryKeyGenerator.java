package com.lonbon.cloud.base.entity;

import com.easy.query.core.basic.extension.generated.PrimaryKeyGenerator;
import com.github.f4b6a3.uuid.UuidCreator;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class UUIDPrimaryKeyGenerator implements PrimaryKeyGenerator {
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
