package com.lonbon.cloud.user.application.service;

import com.easy.query.core.expression.lambda.SQLActionExpression2;
import com.easy.query.core.expression.lambda.SQLFuncExpression1;
import com.easy.query.core.proxy.SQLSelectExpression;
import com.easy.query.core.proxy.sql.include.IncludeContext;
import com.lonbon.cloud.base.service.ClosureEntityService;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.entity.TenantClosure;
import com.lonbon.cloud.user.domain.entity.proxy.TenantClosureProxy;
import com.lonbon.cloud.user.domain.entity.proxy.TenantProxy;
import com.lonbon.cloud.user.domain.repository.TenantClosureRepository;
import com.lonbon.cloud.user.domain.repository.TenantRepository;
import com.lonbon.cloud.user.domain.service.TenantService;
import io.github.linpeilie.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 租户服务实现类
 * <p>
 * 提供租户的增删改查及层级关系管理功能。
 * 租户层级关系通过闭包表（{@link TenantClosure}）实现，支持多级租户结构。
 * </p>
 *
 * @author stein
 * @since 1.0.0
 * @see ClosureEntityService
 * @see TenantService
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TenantServiceImpl extends ClosureEntityService<Tenant, TenantProxy, TenantClosure, TenantClosureProxy>
        implements TenantService {
    
    /**
     * 构造租户服务实现
     *
     * @param converter          DTO转换器
     * @param repository         租户仓库
     * @param closureRepository  租户闭包表仓库
     */
    public TenantServiceImpl(
            Converter converter, TenantRepository repository, TenantClosureRepository closureRepository) {
        super(converter, repository, closureRepository, Tenant.class);
    }

    /**
     * 获取导航属性表达式
     * <p>
     * 配置租户实体的导航属性，用于关联查询祖先节点。
     * </p>
     *
     * @return 导航属性表达式
     */
    @Override
    protected SQLActionExpression2<IncludeContext, TenantProxy> navigate() {
        return (c, t) -> c.query(t.ancestors());
    }

    /**
     * 设置父ID列的更新表达式
     * <p>
     * 定义更新父ID字段的表达式，用于移动节点操作。
     * </p>
     *
     * @return 父ID列更新表达式
     */
    @Override
    protected SQLFuncExpression1<TenantProxy, SQLSelectExpression> setColumnParentId() {
        return TenantProxy::parentId;
    }


    /**
     * 创建闭包记录
     * <p>
     * 用于在创建租户或移动租户时，创建闭包表记录。
     * </p>
     *
     * @param ancestorId   祖先节点ID
     * @param descendantId 后代节点ID
     * @param distance     距离（层级差）
     * @return 租户闭包记录
     */
    @Override
    protected TenantClosure createClosure(UUID ancestorId, UUID descendantId, Integer distance) {
        return new TenantClosure(ancestorId, descendantId, distance);
    }
}