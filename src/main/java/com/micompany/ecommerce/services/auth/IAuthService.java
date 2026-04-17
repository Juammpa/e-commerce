package com.micompany.ecommerce.services.auth;

import com.micompany.ecommerce.dto.auth.AuthResponseDto;
import com.micompany.ecommerce.dto.auth.LoginRegisterDto;
import com.micompany.ecommerce.dto.auth.RegisterRequestDto;

public interface IAuthService {

    AuthResponseDto register(RegisterRequestDto request);

    AuthResponseDto login(LoginRegisterDto request);

}
