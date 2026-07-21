package com.micompany.ecommerce.dto.auth;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class RegisterRequestDto {

    @NotBlank(message = "The email is mandatory")
    @Email(message = "The email format is invalid")
    private String email;

    @NotBlank(message = "The password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

}
