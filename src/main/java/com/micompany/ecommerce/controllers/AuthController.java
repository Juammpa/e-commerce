package com.micompany.ecommerce.controllers;

import com.micompany.ecommerce.dto.auth.AuthResponseDto;
import com.micompany.ecommerce.dto.auth.LoginRegisterDto;
import com.micompany.ecommerce.dto.auth.RegisterRequestDto;
import com.micompany.ecommerce.services.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid RegisterRequestDto request) {

        return ResponseEntity.status(201).body(authService.register(request));

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginRegisterDto request) {

        return ResponseEntity.ok(authService.login(request));

    }


}
