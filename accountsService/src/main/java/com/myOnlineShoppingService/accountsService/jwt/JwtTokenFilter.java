package com.myOnlineShoppingService.accountsService.jwt;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Verifica si el header contiene el token de autorización
        if (!hasAuthorizationBearer(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getAccessToken(request);

        // Verifica si el token es válido
        if (!jwtTokenUtil.validateAccessToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Si el token es válido, establece el contexto de autenticación
        setAuthenticationContext(token, request);
        filterChain.doFilter(request, response);
    }

    // Verifica si el header tiene un token Bearer
    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return ObjectUtils.isNotEmpty(header) && header.startsWith("Bearer ");
    }

    // Extrae el token JWT del header de autorización
    private String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header.split(" ")[1].trim();
    }

    // Establece el contexto de autenticación si el token es válido
    private void setAuthenticationContext(String token, HttpServletRequest request) {
        String email = jwtTokenUtil.getSubject(token); // Extrae el email
        String role = jwtTokenUtil.getRole(token); // Extrae el rol

        // Valida que email y role no sean null o vacíos
        if (email == null || email.isEmpty() || role == null || role.isEmpty()) {
            throw new IllegalArgumentException("El email o rol no pueden ser nulos o vacíos");
        }

        // Crea un usuario de Spring Security con el rol y el email extraídos del token
        UserDetails userDetails = new User(email, "psw",
                List.of(new SimpleGrantedAuthority("ROLE_" + role)));

        // Crea el token de autenticación
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Establece el contexto de autenticación
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
