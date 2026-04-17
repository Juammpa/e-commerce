package com.micompany.ecommerce.mappers;

import com.micompany.ecommerce.dto.carts.CartItemRequestDto;
import com.micompany.ecommerce.dto.carts.CartItemResponseDto;
import com.micompany.ecommerce.dto.carts.CartResponseDto;
import com.micompany.ecommerce.dto.categories.CategoryResponseDto;
import com.micompany.ecommerce.dto.orders.OrderItemResponseDto;
import com.micompany.ecommerce.dto.orders.OrderResponseDto;
import com.micompany.ecommerce.dto.products.ProductResponseDto;
import com.micompany.ecommerce.models.entities.*;
import com.micompany.ecommerce.models.enums.Status;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class Mapper {

    public static CategoryResponseDto toDTO(Category category) {

        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    public static ProductResponseDto toDTO(Product product) {

        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategory().getId())
                .build();
    }

    public static CartResponseDto cartToResponseDTO(Cart cart) {

        CartResponseDto response = new CartResponseDto();
        response.setId(cart.getId());

        // Mappig items
        List<CartItemResponseDto> itemDtos = cart.getItems().stream().map(item ->{
            //  Mapping CartItem --> CartItemResponseDto
            CartItemResponseDto dto = new CartItemResponseDto();
            dto.setId(item.getId());
            dto.setProductName(item.getProduct().getName());
            dto.setUnitPrice(item.getProduct().getPrice());
            dto.setQuantity(item.getQuantity());
            dto.setSubtotal(item.getProduct().getPrice() * item.getQuantity());
            return dto;
        }).collect(Collectors.toList());

        response.setItems(itemDtos);

        // Calculate total
        double total = itemDtos.stream()
                .mapToDouble(CartItemResponseDto::getSubtotal)
                .sum();
        response.setTotal(total);

        return response;

    }

    public static OrderItem toOrderItem(CartItem cartItem) {

        return OrderItem.builder()
                .product(cartItem.getProduct())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getProduct().getPrice())
                .build();
    }

    public static OrderResponseDto orderToResponseDTO(Order order) {

        OrderResponseDto response = new OrderResponseDto();
        response.setId(order.getId());
        response.setUserEmail(order.getUser().getEmail());
        response.setCreatedAt(order.getCreateAt());
        response.setStatus(order.getStatus().name());

        List<OrderItemResponseDto> itemDtos = order.getItems().stream().map(item -> {

                OrderItemResponseDto dto = new OrderItemResponseDto();
                dto.setId(item.getProduct().getId());
                dto.setProductName(item.getProduct().getName());
                dto.setQuantity(item.getQuantity());
                dto.setPriceAtPurchase(item.getProduct().getPrice());
                return dto;

                }).collect(Collectors.toList());

        response.setItems(itemDtos);

        Double total = itemDtos.stream()
                .mapToDouble(item -> item.getPriceAtPurchase() * item.getQuantity())
                .sum();
        response.setTotal(total);

        return response;
    }

    public static Order cartToOrder (Cart cart) {

        Order order = Order.builder()
                .user(cart.getUser())
                .createAt(LocalDateTime.now())
                .status(Status.PENDING)
                .build();

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = Mapper.toOrderItem(cartItem);
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .toList();

        order.setItems(orderItems);
        order.setTotal(orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum());

        return order;

    }


}
