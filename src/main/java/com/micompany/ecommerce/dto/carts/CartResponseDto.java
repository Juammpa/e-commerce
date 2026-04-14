package com.micompany.ecommerce.dto.carts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDto {

    private Long id;
    private List<CartItemResponseDto> items;
    private Double total;   // Calculated

}
