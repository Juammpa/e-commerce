package com.micompany.ecommerce.services.carts;

import com.micompany.ecommerce.dto.carts.CartItemRequestDto;
import com.micompany.ecommerce.dto.carts.CartItemResponseDto;
import com.micompany.ecommerce.dto.carts.CartResponseDto;

public interface ICartService {

    CartResponseDto getCart(String userEmail);

    CartResponseDto addCartItem(String userEmail, CartItemRequestDto request);

    CartResponseDto updateCartQuantity(String userEmail, Long id, Integer quantity);

    CartResponseDto removeCartItem(String userEmail, Long itemId);

    void deleteCart(String userEmail);


}
