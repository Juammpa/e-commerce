package com.micompany.ecommerce.controllers;

import com.micompany.ecommerce.dto.carts.CartItemRequestDto;
import com.micompany.ecommerce.dto.carts.CartResponseDto;
import com.micompany.ecommerce.services.carts.ICartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private ICartService cartService;

    @GetMapping
    public ResponseEntity<CartResponseDto> getMyCart(Principal principal) {
        // Obtiene el email del usuario logueado
        String userEmail = principal.getName();
        return ResponseEntity.ok(cartService.getCart(userEmail));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addItem(Principal principal, @RequestBody @Valid CartItemRequestDto request) {

        String userEmail = principal.getName();
        return ResponseEntity.ok(cartService.addCartItem(userEmail, request));

    }

    @PutMapping("/items/{id}")
    public ResponseEntity<CartResponseDto> updateCartItem(Principal principal, @PathVariable Long id, @RequestBody Integer quantity) {

        String userEmail = principal.getName();
        return ResponseEntity.ok(cartService.updateCartQuantity(userEmail, id, quantity));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<CartResponseDto> removeProduct(Principal principal, @PathVariable Long id) {
        String userEmail = principal.getName();
        return ResponseEntity.ok(cartService.removeCartItem(userEmail, id));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMyCart(Principal principal) {

        String userEmail = principal.getName();
        cartService.deleteCart(userEmail);
        return ResponseEntity.noContent().build();
    }

}
