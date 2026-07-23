package com.micompany.ecommerce.services.carts;

import com.micompany.ecommerce.dto.carts.CartItemRequestDto;
import com.micompany.ecommerce.dto.carts.CartResponseDto;
import com.micompany.ecommerce.exceptions.InsufficientStockException;
import com.micompany.ecommerce.exceptions.InvalidQuantityException;
import com.micompany.ecommerce.exceptions.ResourceNotFoundException;
import com.micompany.ecommerce.mappers.Mapper;
import com.micompany.ecommerce.models.entities.Cart;
import com.micompany.ecommerce.models.entities.CartItem;
import com.micompany.ecommerce.models.entities.Product;
import com.micompany.ecommerce.models.entities.User;
import com.micompany.ecommerce.repositories.CartRepository;
import com.micompany.ecommerce.repositories.ProductRepository;
import com.micompany.ecommerce.repositories.UserRepository;
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

        // Valida la cantidad ingresada
        validateQuantity(request.getQuantity());

        // Obtener el carrito del usuario
        Cart cart = getOrCreateCart(userEmail);

        // Validar que el producto exista
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product","id", request.getProductId()));

        // Lógica de negocio: ¿El producto ya está en el carrito?
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        // Obtenemos el valor de la cantidad total
        int resultingQuantity = existingItem
                .map(item ->
                        item.getQuantity() + request.getQuantity()
                )
                .orElse(request.getQuantity());

        // Validamos stock
        if (product.getStock() < resultingQuantity) {
            throw new InsufficientStockException(
                    product.getId(),
                    product.getName(),
                    resultingQuantity,
                    product.getStock()
            );
        }

        if(existingItem.isPresent()) {
            // Si ya existe, actualizamos la cantidad
            existingItem.get().setQuantity(resultingQuantity);
        } else {
            // Si no existe, creamos un nuevo item y lo asociamos al carrito
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(resultingQuantity);
            cart.getItems().add(newItem);
        }

        // Guardar en BD (Gracias a CascadeType.ALL, guardará los items automáticamente)
        // Retornar el DTO mapeado
        return Mapper.cartToResponseDTO(cartRepository.save(cart));

    }

    @Override
    @Transactional
    public CartResponseDto updateCartQuantity(String userEmail, Long id, Integer quantity) {

        // Valida la cantidad ingresada
        validateQuantity(quantity);

        // Obtener el carrito del usuario
        Cart cart = getOrCreateCart(userEmail);

        // Buscar el item específico dentro de ese carrito
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));

        // 3. Validar stock (Requisito funcional del PDF)
        if(item.getProduct().getStock() < quantity) {
            throw new InsufficientStockException(item.getProduct().getId(),item.getProduct().getName(),quantity,item.getProduct().getStock());
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
            throw new ResourceNotFoundException("Item", "id", itemId);
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
                orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
    }

    /*
     * Impide utilizar cantidades nulas, iguales a cero o negativas.
     *
     * Esta validación pertenece al servicio porque el servicio podría
     * ser invocado desde un lugar distinto al controlador.
     */
    private void validateQuantity(Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new InvalidQuantityException(quantity);
        }
    }






}

