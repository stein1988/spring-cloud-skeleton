package com.lonbon.cloud.user.domain.service;

import com.lonbon.cloud.base.service.EntityService;
import com.lonbon.cloud.user.domain.entity.User;
import com.lonbon.cloud.user.domain.entity.proxy.UserProxy;

/**
 * 用户服务接口
 * <p>
 * 继承自基础服务接口，提供用户相关的业务操作。
 * </p>
 *
 * @author lonbon
 * @since 1.0.0
 * @see EntityService
 */
public interface UserService extends EntityService<User, UserProxy> {

}
