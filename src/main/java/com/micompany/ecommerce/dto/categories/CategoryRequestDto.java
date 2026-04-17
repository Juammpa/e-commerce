package com.micompany.ecommerce.dto.categories;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class CategoryRequestDto {

    @NotBlank(message = "The name is mandatory")
    @Size(min = 5, message = "The name must be at least 5 characters long")
    private String name;

    private String description;

}
