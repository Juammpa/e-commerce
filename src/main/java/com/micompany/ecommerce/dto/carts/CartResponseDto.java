package com.micompany.ecommerce.dto.carts;

import lombok.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponseDto {

    private Long id;
    private List<CartItemResponseDto> items;
    private Double total = 0.0;  // Calculated

}
