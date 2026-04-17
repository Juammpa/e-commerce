package com.micompany.ecommerce.services.carts;

import com.micompany.ecommerce.dto.carts.CartItemRequestDto;
import com.micompany.ecommerce.dto.carts.CartResponseDto;
import com.micompany.ecommerce.mappers.Mapper;
import com.micompany.ecommerce.models.entities.Cart;
import com.micompany.ecommerce.models.entities.CartItem;
import com.micompany.ecommerce.models.entities.Product;
import com.micompany.ecommerce.models.entities.User;
import com.micompany.ecommerce.repositories.CartRepository;
import com.micompany.ecommerce.repositories.ProductRepository;
import com.micompany.ecommerce.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartService implements ICartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public CartResponseDto getCart(String userEmail) {

        Cart cart = getOrCreateCart(userEmail);
        return Mapper.cartToResponseDTO(cart);
    }

    @Override
    @Transactional
    public CartResponseDto addCartItem(String userEmail, CartItemRequestDto request) {

        // 1. Obtener el carrito del usuario
        Cart cart = getOrCreateCart(userEmail);

        // 2. Validar que el producto exista
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product with ID: " + request.getProductId() + " not found."));

        // 3. Lógica de negocio: ¿El producto ya está en el carrito?
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if(existingItem.isPresent()) {
            // Si ya existe, solo sumamos la cantidad
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            // Si no existe, creamos un nuevo item y lo asociamos al carrito
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.getQuantity());
            cart.getItems().add(newItem);
        }

        // 4. Guardar en BD (Gracias a CascadeType.ALL, guardará los items automáticamente)
        // 5. Retornar el DTO mapeado
        return Mapper.cartToResponseDTO(cartRepository.save(cart));

    }

    @Override
    @Transactional
    public CartResponseDto updateCartQuantity(String userEmail, Long id, Integer quantity) {
        // 1. Obtener el carrito del usuario
        Cart cart = getOrCreateCart(userEmail);

        // 2. Buscar el item específico dentro de ese carrito
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Item with ID: " + id + " not found."));

        // 3. Validar stock (Requisito funcional del PDF)
        if(item.getProduct().getStock() < quantity) {
            throw new RuntimeException("Insuficient stock for product: " + item.getProduct().getName());
        }

        // 4. Actualizar cantidad y guardar
        item.setQuantity(quantity);
        return Mapper.cartToResponseDTO(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponseDto removeCartItem(String userEmail,Long itemId) {
        Cart cart = getOrCreateCart(userEmail);

        // Eliminar el item usando removeIf (Programación funcional)
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(itemId));

        if (!removed) {
            throw new EntityNotFoundException("Item not found in cart");
        }

        return Mapper.cartToResponseDTO(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void deleteCart(String userEmail) {
        Cart cart = getOrCreateCart(userEmail);

        // Al usar orphanRemoval = true en la entidad Cart,
        // limpiar la lista eliminará los items de la base de datos.
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    // ====================================================================
    // MÉTODOS PRIVADOS DE APOYO (Helpers)
    // ====================================================================

    // Busca el carrito. Si el usuario es nuevo y no tiene, le crea uno vacío
    private Cart getOrCreateCart(String userEmail) {

        User user = userRepository.findByEmail(userEmail).
                orElseThrow(() -> new EntityNotFoundException(
                        "User with email: " + userEmail + " not found."));

        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }






}

