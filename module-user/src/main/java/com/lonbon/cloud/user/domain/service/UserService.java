package com.lonbon.cloud.user.domain.service;

import com.lonbon.cloud.base.service.Service;
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
 * @see Service
 */
public interface UserService extends Service<User, UserProxy> {

}
