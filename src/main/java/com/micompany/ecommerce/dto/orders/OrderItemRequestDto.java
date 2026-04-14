package com.micompany.ecommerce.dto.orders;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemRequestDto {

    @NotEmpty(message = "The product ID is mandatory")
    private Long productId;

    @NotEmpty(message = "The quantity is mandatory")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotEmpty(message = "The order ID is mandatory")
    private Long orderId;
}
