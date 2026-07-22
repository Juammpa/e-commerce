package com.micompany.ecommerce.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 * Este filtro se ejecuta una sola vez por cada petición HTTP.
 *
 * Su responsabilidad es:
 *
 * 1. Buscar el header Authorization.
 * 2. Extraer el JWT.
 * 3. Validar el token.
 * 4. Buscar el usuario correspondiente.
 * 5. Registrar la autenticación dentro de Spring Security.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;  // Lee y valida el JWT
    private final CustomUserDetailsService userDetailsService;    // Carga al usuario desde la BD
    /*
     * Componente encargado de responder 401 cuando el token
     * es inválido, está vencido o pertenece a un usuario inexistente.
     */
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        /*
         * El JWT debe enviarse de esta manera:
         *
         * Authorization: Bearer eyJhbGciOi...
         */
        String authHeader = request.getHeader("Authorization");

        /*
         * Si no existe el header o no comienza con "Bearer ",
         * no intentamos autenticar desde este filtro.
         *
         * La petición continúa:
         *
         * - Si el endpoint es público, podrá ejecutarse.
         * - Si está protegido, Spring responderá 401 mediante
         *   JwtAuthenticationEntryPoint.
         */
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Eliminamos el prefijo Bearer para conservar el contenido del token
            String token = authHeader.substring(7);

            // Extraemos el email del JWT
            String email = jwtService.extractEmail(token);

            /*
             * Solamente autenticamos cuando:
             *
             * - El token contiene un email.
             * - Todavía no hay una autenticación configurada.
             */
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Buscamos al usuario en la BD y obtenemos sus permisos
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                /*
                 * Verificamos que:
                 *
                 * - El email del token coincida con el usuario.
                 * - El token no esté vencido.
                 * - La firma sea válida.
                 */
                if(jwtService.isTokenValid(token, userDetails)) {

                    // Creamos el objeto de autenticacion reconocido por Spring Security
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Agregamos detalles propios de la peticion HTTP
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Registramos al usuario como autenticado durante la peticion actual
                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);
                }
            }

            // Continuamos hacia el siguiente filtro o controlador
            filterChain.doFilter(request, response);
        } catch (JwtException
                 | IllegalArgumentException
                 | AuthenticationException exception) {

            // Eliminamos cualquier autenticacion parcial
            SecurityContextHolder.clearContext();

            // Respondemos 401 en formato JSON
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException(
                            "Invalid JWT token",
                            exception
                    )
            );
        }
    }
}
