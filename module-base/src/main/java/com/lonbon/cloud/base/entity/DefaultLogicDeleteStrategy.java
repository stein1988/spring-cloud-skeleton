package com.lonbon.cloud.base.entity;


import com.easy.query.core.basic.extension.logicdel.LogicDeleteBuilder;
import com.easy.query.core.basic.extension.logicdel.abstraction.AbstractLogicDeleteStrategy;
import com.easy.query.core.expression.lambda.SQLActionExpression1;
import com.easy.query.core.expression.parser.core.base.ColumnSetter;
import com.easy.query.core.expression.parser.core.base.WherePredicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Set;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DefaultLogicDeleteStrategy extends AbstractLogicDeleteStrategy {
//    private final CurrentUser currentUser;
    @Override
    public String getStrategy() {
        return "DEFAULT_LOGIC_DELETE_STRATEGY";//后续用户指定逻辑删除名称就是用这个名称即可
    }

    @Override
    public Set<Class<?>> allowedPropertyTypes() {
        return Set.of(Boolean.class);
    }

    /**
     * 逻辑删除的情况下，select操作需要添加的查询过滤器
     */
    @Override
    protected SQLActionExpression1<WherePredicate<Object>> getPredicateFilterExpression(LogicDeleteBuilder builder, String propertyName) {
        return o->o.eq(propertyName, false);
    }

    /**
     * 进行逻辑删除的update操作时，需要set哪些值，这里返回一个lambda表达式，所以set的值都要写在表达式里面动态获取，不要用局部变量，用局部变量的话，就变成一个固定值了
     */
    @Override
    protected SQLActionExpression1<ColumnSetter<Object>> getDeletedSQLExpression(LogicDeleteBuilder builder, String propertyName) {
        return o->o.set(propertyName, true).set("deleteTime", OffsetDateTime.now());
    }


//    @Override
//    protected SQLActionExpression1<WherePredicate<Object>> getPredicateFilterExpression(LogicDeleteBuilder builder, String propertyName) {
//        return o -> o.eq(propertyName, false);
//    }
//
//    @Override
//    protected SQLActionExpression1<ColumnSetter<Object>> getDeletedSQLExpression(LogicDeleteBuilder builder, String propertyName) {
//        //表达式内部的参数不可以提取出来,如果提取出来那么就确定了,而不是实时的 如果一定要提取出来请参考下面的方法
//        return o -> o.set(propertyName, true).set("deleteBy",currentUser.getUserId()).set("deleteTime", LocalDateTime.now());
//    }

    //@Override
    //protected SQLActionExpression1<ColumnSetter<Object>> getDeletedSQLExpression(LogicDeleteBuilder builder, String propertyName) {
    //    //表达式内部的参数不可以提取出来,如果提取出来那么就确定了,而不是实时的
    //    return o -> {
    //        //如果判断动态条件过于复杂可以通过大括号来实现内部的编程而不是链式
    //        //在这边可以提取对应的表达式参数
    //            String userId=currentUser.getUserId();
    //            o.set(propertyName, true).set("deleteBy",userId).set("deleteTime", LocalDateTime.now());
    //    };
    //}
}
