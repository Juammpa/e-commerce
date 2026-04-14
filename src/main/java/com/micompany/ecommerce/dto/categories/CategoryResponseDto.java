package com.micompany.ecommerce.dto.categories;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class CategoryResponseDto {

    private Long id;
    private String name;
    private String description;

}
