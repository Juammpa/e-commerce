package com.micompany.ecommerce.dto.products;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {

    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private Long categoryId;

}
