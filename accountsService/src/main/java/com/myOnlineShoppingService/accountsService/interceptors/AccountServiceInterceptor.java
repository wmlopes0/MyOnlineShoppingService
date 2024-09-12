package com.myOnlineShoppingService.accountsService.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AccountServiceInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.info("Método HTTP: " + request.getMethod());
        logger.info("Ruta solicitada: " + request.getRequestURI());
        logger.info("Parámetros de consulta: " + request.getQueryString());
        logger.info("IP de origen: " + request.getRemoteAddr());
        response.addHeader("accounts-request-reviewed", "true");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
