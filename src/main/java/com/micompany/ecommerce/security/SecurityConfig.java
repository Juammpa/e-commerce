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

/*
 * Configuración central de autenticación y autorización.
 *
 * Aquí se define:
 *
 * - Qué endpoints son públicos.
 * - Qué endpoints requieren CUSTOMER.
 * - Qué endpoints requieren ADMIN.
 * - Cómo se responden los errores 401 y 403.
 * - Que la aplicación utiliza sesiones stateless con JWT.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Filtro encargado de autenticar los JWT
    private final JwtAuthFilter jwtAuthFilter;

    // Servicio encargado de cargar usuarios por email
    private final CustomUserDetailsService userDetailsService;

    // Genera respuestas 401 Unauthorized
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    // Genera respuestas 403 Forbidden
    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {

        http
                // Deshabilitamos ya que no usamos cookies ni sesiones.
                .csrf(c -> c.disable())

                // Reglas de autorizacion de endpoints
                .authorizeHttpRequests(auth -> auth

                        // =================================================
                        // Endpoints públicos
                        // =================================================

                        // Registro publico de clientes
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/register"
                        ).permitAll()

                        // Inicio de sesión.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/login"
                        ).permitAll()

                        // Consulta pública de categorías.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/categories"
                        ).permitAll()

                        // Consulta pública del listado de productos.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/products"
                        ).permitAll()

                        // Consulta pública de un producto.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/products/*"
                        ).permitAll()

                        // =================================================
                        // Endpoints exclusivos de ADMIN
                        // =================================================

                        // Gestion de categorias.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/categories"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/categories/**"
                        ).hasRole("ADMIN")

                        // Gestión de productos.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/products"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/products/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/products/**"
                        ).hasRole("ADMIN")

                        // Consulta general de órdenes.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/orders"
                        ).hasRole("ADMIN")

                        // Actualización del estado de una orden.
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/orders/*/status"
                        ).hasRole("ADMIN")

                        // =================================================
                        // Endpoints exclusivos de CUSTOMER
                        // =================================================
                        // Consulta del carrito propio.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/cart"
                        ).hasRole("CUSTOMER")

                        // Agregar productos al carrito.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/cart/items"
                        ).hasRole("CUSTOMER")

                        // Modificar cantidades del carrito.
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/cart/items/*"
                        ).hasRole("CUSTOMER")

                        // Eliminar un producto del carrito.
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/cart/items/*"
                        ).hasRole("CUSTOMER")

                        // Vaciar el carrito.
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/cart"
                        ).hasRole("CUSTOMER")

                        // Crear una orden desde el carrito.
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/orders"
                        ).hasRole("CUSTOMER")

                        // Consultar las órdenes del usuario autenticado.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/orders/my-orders"
                        ).hasRole("CUSTOMER")

                        // =================================================
                        // Endpoints compartidos
                        // =================================================

                        /*
                         * ADMIN puede consultar cualquier orden.
                         *
                         * CUSTOMER puede utilizar este endpoint, pero
                         * OrderService debe garantizar que la orden
                         * le pertenezca.
                         */
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/orders/*"
                        ).hasAnyRole("ADMIN", "CUSTOMER")

                        /*
                         * Cualquier endpoint que no coincida con las
                         * reglas anteriores requiere autenticación.
                         */
                        .anyRequest().authenticated()
                )

                // La aplicacion no guarda sesiones en el servidor
                // Cada peticion debe enviar su propio JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        ))
                /*
                 * Configuramos las respuestas personalizadas:
                 * - Falta autenticación o token inválido → 401.
                 * - Usuario sin el rol necesario         → 403.
                 */
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                // Configuramos el servicio que Spring Security utiliza para obtener usuarios por email
                .userDetailsService(userDetailsService)
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    /*
     * AuthenticationManager procesa las credenciales del login
     * utilizando la configuración de Spring Security.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) {
        return config.getAuthenticationManager();
    }

    /*
     * BCrypt genera hashes seguros para almacenar contraseñas.
     * Nunca deben guardarse contraseñas en texto plano.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}




