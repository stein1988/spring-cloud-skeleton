package com.lonbon.cloud.user.domain.service;

import com.lonbon.cloud.user.domain.dto.LoginRequest;
import com.lonbon.cloud.user.domain.dto.LoginResponse;
import com.lonbon.cloud.user.domain.entity.User;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
