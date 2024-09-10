package com.myOnlineShoppingService.accountsService.filters;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("prod")
public class IpFilter implements Filter {

    private static final List<String> ALLOWED_IPS = Arrays.asList(
            "192.168.1.100",
            "192.168.1.101");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String clientIp = request.getRemoteAddr();

        if (ALLOWED_IPS.contains(clientIp)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Forbidden: Your IP address is not allowed.");
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
