package com.myOnlineShoppingService.accountsService.config;

import com.myOnlineShoppingService.accountsService.jwt.JwtTokenFilter;
import com.myOnlineShoppingService.accountsService.models.ERole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class ApplicationSecurity {

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails cajero = User.withUsername("Cajero")
                .password(passwordEncoder().encode("cajero"))
                .roles(ERole.CAJERO.name())
                .build();

        UserDetails director = User.withUsername("Director")
                .password(passwordEncoder().encode("director"))
                .roles(ERole.DIRECTOR.name())
                .build();

        return new InMemoryUserDetailsManager(cajero, director);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((requests) -> requests
                        .antMatchers("/auth/login", "/docs/**", "/swagger-ui/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/accounts/**").hasRole(ERole.CAJERO.name()) // Cajeros pueden leer datos de cuentas
                        .antMatchers("/accounts/**").hasRole(ERole.DIRECTOR.name()) // Directores pueden realizar todas las acciones
                        .anyRequest().authenticated()
                )
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(
                        (request, response, ex) -> {
                            response.sendError(
                                    HttpServletResponse.SC_UNAUTHORIZED,
                                    ex.getMessage()
                            );
                        }
                ));

        // Filtro de JWT antes de UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
