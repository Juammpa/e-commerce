package com.micompany.ecommerce.dto.categories;


import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CategoryResponseDto {

    private Long id;
    private String name;
    private String description;

}
