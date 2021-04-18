package com.ihouse.backend.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//配置拦截器
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //不拦截管理员界面和用户的登录、注册页
        registry.addInterceptor(new UserTokenInterceptor())
                .addPathPatterns("/**").excludePathPatterns(
                "/error","/admin/**","/user/login", "/user/register","/house/**","/address/**","/house");
        registry.addInterceptor(new AdminTokenInterceptor())
                .addPathPatterns("/admin/**").excludePathPatterns("/admin/login");
    }
}
