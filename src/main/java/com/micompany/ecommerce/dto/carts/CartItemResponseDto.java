package com.micompany.ecommerce.dto.carts;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponseDto {

    private Long id;
    private String productName;
    private Double unitPrice;
    private Integer quantity;
    private Double subtotal;
}
