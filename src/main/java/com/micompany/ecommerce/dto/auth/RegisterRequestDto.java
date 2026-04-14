package com.micompany.ecommerce.dto.auth;

import com.micompany.ecommerce.models.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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

    @NotEmpty(message = "The role is mandatory")
    private Rol rol;


}
