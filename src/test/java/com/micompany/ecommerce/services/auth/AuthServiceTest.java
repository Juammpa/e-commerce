package com.micompany.ecommerce.services.auth;

import com.micompany.ecommerce.dto.auth.AuthResponseDto;
import com.micompany.ecommerce.dto.auth.LoginRegisterDto;
import com.micompany.ecommerce.dto.auth.RegisterRequestDto;
import com.micompany.ecommerce.models.entities.User;
import com.micompany.ecommerce.repositories.UserRepository;
import com.micompany.ecommerce.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
        testUser.setEmail(email);
        testUser.setPassword(password);
    }

    // ============ Pruebas metodo register() =========
    @Test
    void register_debeDarError_cuandoEmailYaExiste() {

        // Arrange
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {

            authService.register(requestDto);
        });

    }

    @Test
    void register_debeRegistrarUsuario() {

        // Arrange
        RegisterRequestDto requestDto = new RegisterRequestDto();
        requestDto.setEmail(email);
        requestDto.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(password);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(email)).thenReturn("jwt.token.falso");

        // Act
        AuthResponseDto result = authService.register(requestDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals("jwt.token.falso", result.getToken());

    }

    // ============= Pruebas metodo login() ================
    @Test
    void login_debeDarEror_cuandoUsuarioNoExiste() {

        // Arrange
        LoginRegisterDto requestDto = new LoginRegisterDto();
        requestDto.setEmail(email);
        requestDto.setPassword(password);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authenticationToken);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {

            authService.login(requestDto);
        });

    }

    @Test
    void login_debePermitirLogin_cuandoUsuarioExiste() {

        // Arrange
        LoginRegisterDto requestDto = new LoginRegisterDto();
        requestDto.setEmail(email);
        requestDto.setPassword(password);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authenticationToken);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(email)).thenReturn("jwt.token.falso");

        // Act
        AuthResponseDto result = authService.login(requestDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals("jwt.token.falso", result.getToken());
    }
}