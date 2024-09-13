package com.myOnlineShoppingService.accountsService.config;

import com.myOnlineShoppingService.accountsService.interceptors.AccountServiceInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Profile("prod")
public class InterceptorsConfig implements WebMvcConfigurer {
    @Autowired
    private AccountServiceInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor).addPathPatterns("/account/**");;
    }

}
