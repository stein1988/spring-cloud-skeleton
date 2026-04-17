package com.lonbon.cloud.user.infrastructure.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.sign.template.SaSignUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.lonbon.cloud.user.domain.entity.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class SaTokenInterceptor implements WebMvcConfigurer {

    // TODO：是否要修改框架匹配规则，改为正则匹配  https://sa-token.cc/doc.html#/api/sa-strategy
    private final static String API = "/api/*/v1";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor(_ -> {

                    // TODO：警告：允许所有接口，仅调试用
                    SaRouter.match(true).check(_ -> {
                    }).stop();

                    // auth相关接口，必须使用参数sign的方式鉴权，也只需要这种方式
                    SaRouter.match(API + "/auth/**").check(_ -> SaSignUtil.checkRequest(SaHolder.getRequest())).stop();

                    // super admin，放行全部接口
                    SaRouter.match(true).check(_ -> StpUtil.checkRole(Role.SUPER_ADMIN)).stop();

                    // tenant admin，放行相关tenant的所有API接口
                    // TODO：tenant、user相关接口，要做特殊处理，未来有一些全局的接口，要排除
                    SaRouter.match(API + "/**").check(_ -> StpUtil.checkRole(Role.TENANT_ADMIN)).stop();

                    // 根据 api/{module}/v1/{resource}/{action} , 获取权限code：{module}:{resource}:{action}
                    SaRouter.match(API + "/**").check(_ -> StpUtil.checkPermission(getPermissionCode()));

                })).addPathPatterns("/**")
                .excludePathPatterns("/favicon.ico", "/v3/api-docs", "/actuator", "/actuator/**", "/resource/sse");
    }

    private String getPermissionCode() {
        SaRequest request = SaHolder.getRequest();
        String url = request.getUrl();
        String method = request.getMethod();

        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }

        String[] segments = url.split("/");

        String module = "";
        String resource = "";
        String action = "";

        int apiIndex = -1;
        int v1Index = -1;
        for (int i = 0; i < segments.length; i++) {
            if ("api".equals(segments[i]) && apiIndex == -1) {
                apiIndex = i;
            }
            if ("v1".equals(segments[i]) && v1Index == -1) {
                v1Index = i;
            }
        }

        if (apiIndex >= 0 && apiIndex + 1 < segments.length) {
            module = segments[apiIndex + 1];
        }

        if (v1Index >= 0 && v1Index + 1 < segments.length) {
            resource = segments[v1Index + 1];
        }

        String lastSegment = segments.length > 0 ? segments[segments.length - 1] : "";
        if ("delete".equalsIgnoreCase(lastSegment)) {
            action = "delete";
        } else if ("update".equalsIgnoreCase(lastSegment)) {
            action = "update";
        } else if ("add".equalsIgnoreCase(lastSegment)) {
            action = "add";
        } else if ("GET".equalsIgnoreCase(method)) {
            action = "query";
        } else if ("POST".equalsIgnoreCase(method)) {
            action = "add";
        } else if ("PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
            action = "update";
        } else if ("DELETE".equalsIgnoreCase(method)) {
            action = "delete";
        }

        String code = module + ":" + resource + ":" + action;
        log.info(code);
        return code;
    }
}
