package com.myOnlineShoppingService.accountsService.controllers;

import com.myOnlineShoppingService.accountsService.jwt.JwtTokenUtil;
import com.myOnlineShoppingService.accountsService.models.AuthRequest;
import com.myOnlineShoppingService.accountsService.models.AuthResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AuthController {
    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JwtTokenUtil jwtUtil;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
        try {
            // Autenticar al usuario con el AuthenticationManager
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Extraer los detalles del usuario autenticado
            User user = (User) authentication.getPrincipal();

            // Crear un mapa con los claims del JWT
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", user.getUsername());
            claims.put("roles", user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            // Generar el token JWT usando los claims
            String accessToken = jwtUtil.generateAccessToken(claims);

            // Crear la respuesta con el token generado
            AuthResponse response = new AuthResponse(user.getUsername(), accessToken);

            // Retornar la respuesta con el token
            return ResponseEntity.ok().body(response);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
