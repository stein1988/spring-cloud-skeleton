package com.lonbon.cloud.user.application.service;

import com.easy.query.core.api.pagination.EasyPageResult;
import com.lonbon.cloud.base.dto.PageResult;
import com.lonbon.cloud.base.dto.Pageable;
import com.lonbon.cloud.user.domain.dto.TenantCreateDTO;
import com.lonbon.cloud.user.domain.dto.TenantUpdateDTO;
import com.lonbon.cloud.user.domain.entity.Tenant;
import com.lonbon.cloud.user.domain.entity.TenantClosure;
import com.lonbon.cloud.user.domain.repository.TenantClosureRepository;
import com.lonbon.cloud.user.domain.repository.TenantRepository;
import com.lonbon.cloud.user.domain.service.TenantService;
import com.lonbon.cloud.base.exception.BusinessException;
import com.lonbon.cloud.base.exception.ErrorCode;
import io.github.linpeilie.Converter;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 租户服务实现类
 * <p>
 * 提供租户的增删改查及层级关系管理功能。
 * 租户层级关系通过闭包表（TenantClosure）实现，支持多级租户结构。
 * </p>
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TenantServiceImpl implements TenantService {

    /**
     * 租户仓储
     */
    @Resource
    private TenantRepository tenantRepository;

    /**
     * 租户闭包关系仓储
     */
    @Resource
    private TenantClosureRepository tenantClosureRepository;

    /**
     * 对象转换器，用于DTO与实体之间的转换
     */
    @Resource
    private Converter converter;

    /**
     * 创建租户
     * <p>
     * 创建租户时会自动处理租户层级关系：
     * <ol>
     *   <li>保存租户基本信息</li>
     *   <li>创建自身闭包关系（ancestorId=descendantId, distance=0）</li>
     *   <li>若指定了祖先租户，则创建与祖先的直接关系（distance=1）</li>
     * </ol>
     * </p>
     *
     * @param tenant 租户创建信息
     * @return 创建成功的租户实体
     * @throws BusinessException 当指定的祖先租户不存在时抛出
     */
    @Override
    public Tenant createTenant(TenantCreateDTO tenant) {
        // 保存租户基本信息
        Tenant createdTenant = tenantRepository.insert(converter.convert(tenant, Tenant.class));

        // 创建自身闭包关系（distance=0，表示节点自身）
        tenantClosureRepository.insert(createdTenant.createSelfClosure());

        // 处理租户层级关系
        UUID ancestorId = tenant.getAncestorId();
        if (ancestorId != null) {
            // 校验祖先租户是否存在
            Tenant ancestorTenant = tenantRepository.findById(ancestorId)
                    .orElseThrow(() -> new BusinessException(
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "上级租户不存在，ID: " + ancestorId));

            // 创建与祖先的直接父子关系（distance=1，表示直接子节点）
            tenantClosureRepository.insert(new TenantClosure(ancestorId, createdTenant.getId(), 1));
        }

        return createdTenant;
    }

    /**
     * 更新租户信息
     * <p>
     * 根据租户ID更新租户的基本信息。更新前会校验租户是否存在，
     * 若不存在则抛出业务异常。
     * </p>
     *
     * @param id     租户ID，不能为空
     * @param tenant 租户更新信息，包含需要更新的字段
     * @return 更新后的租户实体
     * @throws BusinessException 当租户不存在时抛出，错误码为 {@link ErrorCode#RESOURCE_NOT_FOUND}
     */
    @Override
    public Tenant updateTenant(UUID id, TenantUpdateDTO tenant) {
        // TODO：封装函数
        Tenant existingTenant = tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "租户不存在，ID: " + id));

        Tenant update = converter.convert(tenant, existingTenant);
        return tenantRepository.save(update);
    }

    /**
     * 根据ID删除租户
     *
     * @param id 租户ID
     */
    @Override
    public void deleteTenant(UUID id) {
        tenantRepository.deleteById(id);
    }

    /**
     * 根据ID查询租户
     *
     * @param id 租户ID
     * @return 租户实体（可能为空）
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Tenant> getTenantById(UUID id) {
        return tenantRepository.findById(id);
    }

    /**
     * 根据名称查询租户
     *
     * @param name 租户名称
     * @return 租户实体（可能为空）
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Tenant> getTenantByName(String name) {
        return tenantRepository.singleOptional(o->o.name().eq(name));
    }

    /**
     * 查询所有租户
     *
     * @return 租户列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<Tenant> getAllTenants() {
        return (List<Tenant>) tenantRepository.findAll();
    }

    /**
     * 分页查询租户
     *
     * @param whereObject 查询条件对象
     * @param pageable     分页参数
     * @return 分页结果
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<Tenant> getTenants(Object whereObject, Pageable pageable) {
        return tenantRepository.findPagination(whereObject, pageable);
    }
}