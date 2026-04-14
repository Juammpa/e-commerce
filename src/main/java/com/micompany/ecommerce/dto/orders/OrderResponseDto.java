package com.micompany.ecommerce.dto.orders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {

    private Long id;
    private String userEmail;
    private String status; // Ej: PENDING, CONFIRMED
    private Double total;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDto> items;


}
