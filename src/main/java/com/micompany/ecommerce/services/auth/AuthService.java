package com.micompany.ecommerce.services.auth;

import com.micompany.ecommerce.dto.auth.AuthResponseDto;
import com.micompany.ecommerce.dto.auth.LoginRegisterDto;
import com.micompany.ecommerce.dto.auth.RegisterRequestDto;
import com.micompany.ecommerce.exceptions.EmailAlreadyExistsException;
import com.micompany.ecommerce.exceptions.ResourceNotFoundException;
import com.micompany.ecommerce.models.entities.User;
import com.micompany.ecommerce.models.enums.Rol;
import com.micompany.ecommerce.repositories.UserRepository;
import com.micompany.ecommerce.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Override
    public AuthResponseDto register(RegisterRequestDto request) {

        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRol(Rol.CUSTOMER);

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponseDto(token);

    }

    @Override
    public AuthResponseDto login(LoginRegisterDto request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User","email",request.getEmail()));

        String token = jwtService.generateToken(user.getEmail());
        return new AuthResponseDto(token);

    }
}
