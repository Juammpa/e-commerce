package com.micompany.ecommerce.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class LoginRegisterDto {

    @NotBlank(message = "The email is mandatory")
    @Email(message = "The email format is invalid")
    private String email;

    @NotBlank(message = "The password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

}
