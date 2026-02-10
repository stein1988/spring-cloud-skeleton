package com.lonbon.cloud.demo;

import com.lonbon.cloud.base.exception.BaseBusinessException;
import com.lonbon.cloud.base.exception.ErrorCode;
import com.lonbon.cloud.common.exception.BusinessException;
import com.lonbon.cloud.common.exception.ParameterException;
import com.lonbon.cloud.common.utils.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 异常处理测试控制器
 * 用于测试各种异常情况的处理
 */
@RestController
public class ExceptionTestController {
    
    /**
     * 测试业务异常
     * @return 响应
     */
    @GetMapping("/test/business-exception")
    public Response<?> testBusinessException() {
        throw new BusinessException(ErrorCode.BUSINESS_ERROR, "测试业务异常");
    }
    
    /**
     * 测试参数异常
     * @return 响应
     */
    @GetMapping("/test/parameter-exception")
    public Response<?> testParameterException() {
        throw new ParameterException(ErrorCode.PARAMETER_ERROR, "测试参数异常");
    }
    
    /**
     * 测试资源不存在异常
     * @param id 资源ID
     * @return 响应
     */
    @GetMapping("/test/not-found/{id}")
    public Response<?> testNotFoundException(@PathVariable Long id) {
        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "资源不存在: " + id);
    }
    
    /**
     * 测试权限不足异常
     * @return 响应
     */
    @GetMapping("/test/permission-denied")
    public Response<?> testPermissionDenied() {
        throw new BusinessException(ErrorCode.PERMISSION_DENIED, "权限不足");
    }

    /**
     * 测试系统异常
     * @return 响应
     */
    @GetMapping("/test/system-exception")
    public Response<?> testSystemException() {
        // 模拟空指针异常
        String str = null;
        return Response.success(str.length());
    }
}