package com.micompany.ecommerce.mappers;

import com.micompany.ecommerce.dto.carts.CartItemRequestDto;
import com.micompany.ecommerce.dto.carts.CartItemResponseDto;
import com.micompany.ecommerce.dto.carts.CartResponseDto;
import com.micompany.ecommerce.dto.categories.CategoryResponseDto;
import com.micompany.ecommerce.dto.products.ProductResponseDto;
import com.micompany.ecommerce.models.entities.Cart;
import com.micompany.ecommerce.models.entities.Category;
import com.micompany.ecommerce.models.entities.Product;

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

}
