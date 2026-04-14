package com.micompany.ecommerce.dto.carts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDto {

    private Long id;
    private String productName;
    private Double unitPrice;
    private Integer quantity;
    private Double subtotal;
}
