package com.lonbon.cloud.user.domain.service;

import com.lonbon.cloud.base.service.ClosureService;
import com.lonbon.cloud.user.domain.entity.Department;
import com.lonbon.cloud.user.domain.entity.DepartmentClosure;
import com.lonbon.cloud.user.domain.entity.proxy.DepartmentClosureProxy;
import com.lonbon.cloud.user.domain.entity.proxy.DepartmentProxy;

public interface DepartmentService extends ClosureService<Department, DepartmentProxy, DepartmentClosure, DepartmentClosureProxy> {

}
