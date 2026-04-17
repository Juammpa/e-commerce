package com.micompany.ecommerce.controllers;

import com.micompany.ecommerce.dto.orders.OrderResponseDto;
import com.micompany.ecommerce.dto.orders.OrderStatusUpdateDto;
import com.micompany.ecommerce.models.entities.Order;
import com.micompany.ecommerce.models.enums.Status;
import com.micompany.ecommerce.services.orders.IOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(Principal principal) {

        String userEmail = principal.getName();
        return ResponseEntity.status(201).body(orderService.createOrder(userEmail));
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponseDto>> getMyOrders(Principal principal) {

        String userEmail = principal.getName();
        return ResponseEntity.ok(orderService.getMyOrders(userEmail));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(Principal principal, @PathVariable Long orderId) {

        String userEmail = principal.getName();
        return ResponseEntity.ok(orderService.getOrder(userEmail,orderId));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrderByFilter(@RequestParam(name = "status", required = false) Status filter) {

        return ResponseEntity.ok(orderService.getAllOrders(filter));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(@PathVariable Long orderId, @RequestBody @Valid OrderStatusUpdateDto request) {

        return ResponseEntity.ok(orderService.updateOrderState(orderId, request));

    }


}
