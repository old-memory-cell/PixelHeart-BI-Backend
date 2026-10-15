package com.zzsd.inolibi.config;

import com.zzsd.inolibi.interceptor.TokenIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private TokenIntercepter tokenIntercepter;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenIntercepter).
                addPathPatterns("/**")  //拦截所有请求
                .excludePathPatterns(
                        "/user/login", // 排除登录接口
                        "/user/register", // 排除注册接口
                        "/doc.html",
                        "/webjars/**",
                        "/v3/api-docs/**");

    }


}
