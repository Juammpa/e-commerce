package com.micompany.ecommerce.dto.orders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponseDto {

    private Long id;
    private String productName;
    private Integer quantity;
    private Double priceAtPurchase;
}
