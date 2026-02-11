package com.lonbon.cloud.base.entity;

import com.easy.query.core.basic.extension.interceptor.EntityInterceptor;
import com.easy.query.core.basic.extension.interceptor.UpdateEntityColumnInterceptor;
import com.easy.query.core.basic.extension.interceptor.UpdateSetInterceptor;
import com.easy.query.core.expression.parser.core.base.ColumnOnlySelector;
import com.easy.query.core.expression.parser.core.base.ColumnSetter;
import com.easy.query.core.expression.sql.builder.EntityInsertExpressionBuilder;
import com.easy.query.core.expression.sql.builder.EntityUpdateExpressionBuilder;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DefaultEntityInterceptor implements EntityInterceptor, UpdateSetInterceptor, UpdateEntityColumnInterceptor {

    @Override
    public String name() {
        return "DEFAULT_INTERCEPTOR";    //后续禁用拦截器或者启用拦截器使用这个名称代表当前拦截器
    }

    /**
     * 哪些对象需要用到这个拦截器(继承BaseEntity的对象)
     */
    @Override
    public boolean apply(@NonNull Class<?> entityClass) {
        return BaseEntity.class.isAssignableFrom(entityClass);
    }

    /**
     * insert操作时的处理
     */
    @Override
    public void configureInsert(Class<?> entityClass, EntityInsertExpressionBuilder entityInsertExpressionBuilder, Object entity) {
        BaseEntity baseEntity = (BaseEntity) entity;

        OffsetDateTime now = OffsetDateTime.now();

        if (baseEntity.getCreateTime() == null) {
            baseEntity.setCreateTime(now);
        }
        if (baseEntity.getCreateBy() == null) {
//            String userId = StringUtils.defaultString(currentUser.getUserId());
            //如果使用sa-token这边采用StpUtil.getLoginIdAsString()会让导致程序需要验证
            //,所以这边需要先判断是否登录,未登录就给默认值,不然就获取
            //updateBy同理
//            baseEntity.setCreateBy(userId);
        }
        if (baseEntity.getUpdateTime() == null) {
            baseEntity.setUpdateTime(now);
        }
        if (baseEntity.getUpdateBy() == null) {
//            String userId = StringUtils.defaultString(currentUser.getUserId());
//            baseEntity.setUpdateBy(userId);
        }
    }

    /**
     * update操作时的处理（不包括伪删除）
     */
    @Override
    public void configureUpdate(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, Object entity) {
        BaseEntity baseEntity = (BaseEntity) entity;
        baseEntity.setUpdateTime(OffsetDateTime.now());
//        String userId = StringUtils.defaultString(currentUser.getUserId());
//        baseEntity.setUpdateBy(userId);
    }

    @Override
    public void configure(@NonNull Class<?> entityClass, @NonNull EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, @NonNull ColumnOnlySelector<Object> columnSelector, @NonNull Object entity) {

    }

    @Override
    public void configure(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, ColumnSetter<Object> columnSetter) {

    }



    //如果你是springsecurity可以用这个SecurityContextHolder.getContext()
    //如果你是satoken那么直接用StpUtil
//    private final CurrentUser currentUser;//对springboot进行的封装可以通过jwt获取对应的当前操作人用户

//    /**
//     * 添加默认的数据
//     *
//     * @param entityClass
//     * @param entityInsertExpressionBuilder
//     * @param entity
//     */
//    @Override
//    public void configureInsert(Class<?> entityClass, EntityInsertExpressionBuilder entityInsertExpressionBuilder, Object entity) {
//        BaseEntity baseEntity = (BaseEntity) entity;
//        if (baseEntity.getCreateTime() == null) {
//            baseEntity.setCreateTime(LocalDateTime.now());
//        }
//        if (baseEntity.getCreateBy() == null) {
//            String userId = StringUtils.defaultString(currentUser.getUserId());
//            //如果使用sa-token这边采用StpUtil.getLoginIdAsString()会让导致程序需要验证
//            //,所以这边需要先判断是否登录,未登录就给默认值,不然就获取
//            //updateBy同理
//            baseEntity.setCreateBy(userId);
//        }
//        if (baseEntity.getUpdateTime() == null) {
//            baseEntity.setUpdateTime(LocalDateTime.now());
//        }
//        if (baseEntity.getUpdateBy() == null) {
//            String userId = StringUtils.defaultString(currentUser.getUserId());
//            baseEntity.setUpdateBy(userId);
//        }
//        if (baseEntity.getDeleted() == null) {
//            baseEntity.setDeleted(false);
//        }
//
//        if (baseEntity.getId() == null) {
//            baseEntity.setId(IdHelper.nextId());
//        }
//        //如果你部分对象需要使用雪花id,那么你可以定义一个雪花id的空接口
//        //然后让对象继承这个空接口
//        // if(雪花ID.class.isAssignableFrom(entity.getClass())){
//        //     if (baseEntity.getId() == null) {
//        //         baseEntity.setId(//赋值雪花id);
//        //     }
//        // }else{
//        //     if (baseEntity.getId() == null) {
//        //         baseEntity.setId(IdHelper.nextId());
//        //     }
//        // }
//    }
//
//    /**
//     * 添加更新对象参数
//     *
//     * @param entityClass
//     * @param entityUpdateExpressionBuilder
//     * @param entity
//     */
//    @Override
//    public void configureUpdate(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, Object entity) {
//        BaseEntity baseEntity = (BaseEntity) entity;
//        baseEntity.setUpdateTime(LocalDateTime.now());
//        String userId = StringUtils.defaultString(currentUser.getUserId());
//        baseEntity.setUpdateBy(userId);
//    }
//
//    /**
//     * 表达式更新set参数添加
//     *
//     * @param entityClass
//     * @param entityUpdateExpressionBuilder
//     * @param columnSetter
//     */
//    @Override
//    public void configure(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, ColumnSetter<Object> columnSetter) {
//        //创建两个属性比较器 如果你觉得你程序里面不会手动去修改这两个值那么也可以不加这个判断
//        EntitySegmentComparer updateTime = new EntitySegmentComparer(entityClass, "updateTime");
//        EntitySegmentComparer updateBy = new EntitySegmentComparer(entityClass, "updateBy");
//        columnSetter.getSQLBuilderSegment().forEach(k -> {
//            updateTime.visit(k);
//            updateBy.visit(k);
//            return updateTime.isInSegment() && updateBy.isInSegment();
//        });
//        //是否已经set了
//        if (!updateBy.isInSegment()) {
//            String userId = StringUtils.defaultString(CurrentUserHelper.getUserId());
//            columnSetter.set( "updateBy", userId);
//        }
//        if (!updateTime.isInSegment()) {
//            columnSetter.set("updateTime", LocalDateTime.now());
//        }
//    }
//    /**
//     * 对象属性更新指定列更新
//     *
//     * @param entityClass
//     * @param entityUpdateExpressionBuilder
//     * @param columnSelector
//     * @param entity
//     */
//    @Override
//    public void configure(Class<?> entityClass, EntityUpdateExpressionBuilder entityUpdateExpressionBuilder, ColumnOnlySelector<Object> columnSelector, Object entity) {
//        //创建两个属性比较器
//        EntitySegmentComparer updateTime = new EntitySegmentComparer(entityClass, "updateTime");
//        EntitySegmentComparer updateBy = new EntitySegmentComparer(entityClass, "updateBy");
//        columnSelector.getSQLSegmentBuilder().forEach(k -> {
//            updateTime.visit(k);
//            updateBy.visit(k);
//            return updateTime.isInSegment() && updateBy.isInSegment();
//        });
//        //是否已经set了
//        if (!updateTime.isInSegment()) {
//            columnSelector.column("updateTime");
//        }
//        if (!updateBy.isInSegment()) {
//            columnSelector.column( "updateBy");
//        }
//    }
//
//    @Override
//    public String name() {
//        return "DEFAULT_INTERCEPTOR";//后续禁用拦截器或者启用拦截器使用这个名称代表当前拦截器
//    }
//    /**
//     * 那些对象需要用到这个拦截器(这边设置继承BaseEntity的对象)
//     */
//    @Override
//    public boolean apply(Class<?> entityClass) {
//        return BaseEntity.class.isAssignableFrom(entityClass);
//    }
}
