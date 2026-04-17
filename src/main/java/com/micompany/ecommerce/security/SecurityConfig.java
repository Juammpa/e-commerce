package com.micompany.ecommerce.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {

        http
                .csrf(c -> c.disable())
                // Rules of access
                .authorizeHttpRequests(auth -> auth

                 // ========= Public Path ==========

                        // Authentication
                        .requestMatchers(HttpMethod.POST,"/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/auth/login").permitAll()

                        // Categories
                        .requestMatchers(HttpMethod.GET,"/api/categories").permitAll()

                        // Products
                        .requestMatchers(HttpMethod.GET,"/api/products").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/products/*").permitAll()

                 // ======= Path ADMIN ========

                        // Categories
                        .requestMatchers(HttpMethod.POST,"/api/categories").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/categories/**").hasRole("ADMIN")
                        // Products
                        .requestMatchers(HttpMethod.POST, "/api/products").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                        // Orders
                        .requestMatchers(HttpMethod.GET,"/api/orders").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/orders/*/status").hasRole("ADMIN")

                 // ======== Path CUSTOMER ==============

                        // Cart
                        .requestMatchers(HttpMethod.GET,"/api/cart").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST,"/api/cart/items").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.PUT,"/api/cart/items/*").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE,"/api/cart/items/*").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE,"/api/cart").hasRole("CUSTOMER")

                        // Order
                        .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/my-orders").hasRole("CUSTOMER")

                 // =========== Path ADMIN & CUSTOMER ==========

                        .requestMatchers(HttpMethod.GET, "/api/orders/*").hasAnyRole("ADMIN","CUSTOMER")

                 // Anything else path require authentication
                        .anyRequest().authenticated()

                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                )

                .userDetailsService(userDetailsService)

                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}




