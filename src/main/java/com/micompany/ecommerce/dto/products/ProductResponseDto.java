package com.micompany.ecommerce.dto.products;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponseDto {

    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private Long categoryId;

}
