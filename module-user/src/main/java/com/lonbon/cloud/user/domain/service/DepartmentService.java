package com.lonbon.cloud.user.domain.service;

import com.lonbon.cloud.base.service.ClosureService;
import com.lonbon.cloud.base.service.EntityService;
import com.lonbon.cloud.user.domain.entity.Department;
import com.lonbon.cloud.user.domain.entity.DepartmentClosure;
import com.lonbon.cloud.user.domain.entity.Permission;
import com.lonbon.cloud.user.domain.entity.proxy.DepartmentClosureProxy;
import com.lonbon.cloud.user.domain.entity.proxy.DepartmentProxy;

/**
 * 部门服务接口
 * <p>
 * 继承自闭包服务接口，提供部门相关的业务操作和树形结构操作。
 * 支持查询父子关系、祖先后代关系以及部门移动等功能。
 * </p>
 *
 * @author lonbon
 * @since 1.0.0
 * @see ClosureService
 */
public interface DepartmentService extends EntityService<Department, DepartmentProxy>, ClosureService<Department, DepartmentProxy, DepartmentClosure, DepartmentClosureProxy> {

}
