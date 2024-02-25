package com.commerce.store.ecommerce.backend.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class WebSecurityConfig {

    private JWTRequestFilter jwtRequestFilter;

    public WebSecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    //    When someone send requst to  access to rest api when we process it, if we have Security filterChain it goes through
//     to verify if request is valid before putting it through the controller
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf().disable().cors().disable();
        http.addFilterBefore(jwtRequestFilter, AuthorizationFilter.class);
//        http.authorizeHttpRequests().anyRequest().permitAll();
        http.authorizeHttpRequests()
        .requestMatchers("/product","/auth/register","/auth/login", "/auth/verify", "/auth/forgot", "/auth/reset").permitAll()
//                .requestMatchers("/auth/register").permitAll()
//                .requestMatchers("/auth/login").permitAll()
        .anyRequest().authenticated();
        return http.build();
    }
}
