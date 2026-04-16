package com.lonbon.cloud.base.dto;

import com.lonbon.cloud.user.domain.entity.User;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AutoMapper(target = User.class, reverseConvertGenerate = false)
@Data
@NoArgsConstructor
public class LoginUser implements Serializable {

    public static final String KEY = "loginUser";

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private UUID userId;

    /**
     * 租户ID
     */
    private UUID currentTenantId;

    /**
     * 角色code
     */
    private List<String> roleCodes;

    /**
     * 部门ID
     */
    private UUID deptId;

    /**
     * 部门类别编码
     */
    private String deptCategory;

    /**
     * 部门名
     */
    private String deptName;

    /**
     * 用户唯一标识
     */
    private String token;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 登录时间
     */
    private OffsetDateTime loginTime;

    /**
     * 过期时间
     */
    private OffsetDateTime expireTime;

    /**
     * 登录IP地址
     */
    private String ipaddr;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 浏览器类型
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 菜单权限
     */
    private Set<String> menuPermission;

    /**
     * 角色权限
     */
    private Set<String> rolePermission;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 客户端
     */
    private String clientKey;

    /**
     * 设备类型
     */
    private String deviceType;
}
