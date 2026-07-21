package com.micompany.ecommerce.services.auth;

import com.micompany.ecommerce.dto.auth.AuthResponseDto;
import com.micompany.ecommerce.dto.auth.LoginRegisterDto;
import com.micompany.ecommerce.dto.auth.RegisterRequestDto;
import com.micompany.ecommerce.models.entities.User;
import com.micompany.ecommerce.models.enums.Rol;
import com.micompany.ecommerce.repositories.UserRepository;
import com.micompany.ecommerce.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
 * Esta clase contiene pruebas unitarias de AuthService.
 *
 * Las dependencias externas, como el repositorio, el codificador de
 * contraseñas y JwtService, son reemplazadas por mocks.
 *
 * El objetivo es verificar únicamente la lógica de AuthService.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    /*
     * Mockito crea AuthService e inyecta automáticamente
     * todos los mocks declarados anteriormente.
     */
    @InjectMocks
    private AuthService authService;

    // =========== Metodo Helper ==============

    private String email;
    private String password;
    private User testUser;

    @BeforeEach
    void setUp() {

        email = "example@gmail.com";
        password = "1234567";

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail(email);
        testUser.setPassword("encoded-password");
        testUser.setRol(Rol.CUSTOMER);
    }

    // =========================================================
    // Pruebas del metodo register()
    // =========================================================

    @Test
    void register_debeDarError_cuandoEmailYaExiste() {

        // Arrange: creamos un request con un email ya registrado.
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setEmail(email);
        requestDto.setPassword(password);

        /*
         * Simulamos que el repositorio encuentra un usuario
         * registrado con el mismo email.
         */
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act & Assert: el registro debe ser rechazado.
        assertThrows(RuntimeException.class, () -> {
            authService.register(requestDto);
        });

        /*
         * Si el email ya existe, no se debe:
         *
         * - Codificar la contraseña.
         * - Guardar un usuario.
         * - Generar un JWT.
         */
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateToken(any());


    }

    @Test
    void register_debeRegistrarUsuarioComoCustomer() {

        // Arrange: request valido de registro publico.
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setEmail(email);
        requestDto.setPassword(password);

        // Simulamos que el email todavia no esta registrado.
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Simulamos el resultado de codificar la contraseña
        when(passwordEncoder.encode(password)).thenReturn("encoded-password");

        // Devolvemos el mismo User recibido por el repositorio
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Simulamos la generacion del JWT
        when(jwtService.generateToken(email)).thenReturn("jwt.token.falso");

        // Act: ejecutamos el registro.
        AuthResponseDto result = authService.register(requestDto);

        /*
         * Capturamos el User enviado a userRepository.save().
         *
         * Esto permite verificar exactamente qué datos intentó
         * guardar AuthService.
         */
        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        // Assert: el email guardado debe ser el recibido.
        assertEquals(email, savedUser.getEmail());

        /*
         * La contraseña almacenada debe ser la contraseña codificada
         * y nunca la contraseña original.
         */
        assertEquals("encoded-password", savedUser.getPassword());
        assertNotEquals(password, savedUser.getPassword());

        /*
         * Regla de seguridad principal:
         *
         * Todoo usuario registrado desde el endpoint público
         * debe recibir obligatoriamente el rol CUSTOMER.
         */
        assertEquals(Rol.CUSTOMER, savedUser.getRol());

        // La respuesta debe contener el JWT generado.
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals("jwt.token.falso", result.getToken());

        // Verificamos que las dependencias hayan recibido los datos correctos.
        verify(passwordEncoder).encode(password);
        verify(jwtService).generateToken(email);

    }

    // =========================================================
    // Pruebas del metodo login()
    // =========================================================

    @Test
    void login_debeDarEror_cuandoUsuarioNoExiste() {

        // Arrange: credenciales con formato valido.
        LoginRegisterDto requestDto = new LoginRegisterDto();
        requestDto.setEmail(email);
        requestDto.setPassword(password);

        /*
         * Este token representa las credenciales que AuthService
         * entrega a AuthenticationManager.
         */
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        /*
         * Simulamos una autenticación válida.
         *
         * Después de autenticar, AuthService intenta recuperar
         * el usuario desde el repositorio.
         */
        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authenticationToken);

        /*
         * Simulamos una inconsistencia: las credenciales fueron
         * autenticadas, pero el usuario no aparece en el repositorio.
         */
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert: el login debe ser rechazado.
        assertThrows(RuntimeException.class, () -> {
            authService.login(requestDto);
        });

        // Si el usuario no se encuentra, no se debe generar ningun token.
        verify(jwtService, never()).generateToken(any());

    }

    @Test
    void login_debePermitirLogin_cuandoUsuarioExiste() {

        // Arrange: request de inicio de sesion valido.
        LoginRegisterDto requestDto = new LoginRegisterDto();
        requestDto.setEmail(email);
        requestDto.setPassword(password);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        /*
         * Simulamos que Spring Security valida correctamente
         * las credenciales.
         */
        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authenticationToken);

        // Simulamos que el usuario existe.
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Simulamos la generacion del JWT
        when(jwtService.generateToken(email)).thenReturn("jwt.token.falso");

        // Act: ejecutamos el login
        AuthResponseDto result = authService.login(requestDto);

        // Assert: verificamos la respuesta
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals("jwt.token.falso", result.getToken());

        /*
         * También verificamos que AuthenticationManager haya recibido
         * las credenciales correctas.
         */
        verify(authenticationManager).authenticate(authenticationToken);
        verify(jwtService).generateToken(email);
    }
}