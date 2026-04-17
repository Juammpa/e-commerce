package com.micompany.ecommerce.services.orders;

import com.micompany.ecommerce.dto.orders.OrderItemResponseDto;
import com.micompany.ecommerce.dto.orders.OrderResponseDto;
import com.micompany.ecommerce.dto.orders.OrderStatusUpdateDto;
import com.micompany.ecommerce.models.enums.Status;

import java.util.List;

public interface IOrderService {

    OrderResponseDto createOrder(String userEmail);

    List<OrderResponseDto> getMyOrders(String userEmail);

    OrderResponseDto getOrder(String userEmail, Long orderId);

    List<OrderResponseDto> getAllOrders(Status statusFilter);

    OrderResponseDto updateOrderState(Long id, OrderStatusUpdateDto request);

}
