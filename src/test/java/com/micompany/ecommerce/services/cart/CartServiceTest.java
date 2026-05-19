package com.micompany.ecommerce.services.cart;

import com.micompany.ecommerce.dto.carts.CartItemRequestDto;
import com.micompany.ecommerce.dto.carts.CartResponseDto;
import com.micompany.ecommerce.models.entities.Cart;
import com.micompany.ecommerce.models.entities.CartItem;
import com.micompany.ecommerce.models.entities.Product;
import com.micompany.ecommerce.models.entities.User;
import com.micompany.ecommerce.repositories.CartRepository;
import com.micompany.ecommerce.repositories.ProductRepository;
import com.micompany.ecommerce.repositories.UserRepository;
import com.micompany.ecommerce.services.carts.CartService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)     // Conecta Mockito con JUnit 5
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    // ================ Metodo Helper ==============

    private String email;
    private User testUser;
    private Cart testCart;

    @BeforeEach
    void setUp() {

        email = "example@gmail.com";
        testUser = new User();

        testUser.setEmail(email);
        testCart = new Cart();

        testCart.setUser(testUser);
    }


    // ============ Pruebas metodo getCart() ===============
    @Test
    void getCart_deberiaRetornarCarrito_cuandoUsuarioExiste() {

       // Arrange

        // Le digo al mock, lo que me tiene que devolver
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        // Act
        CartResponseDto result = cartService.getCart(email);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(0.0, result.getTotal());

        // Verificamos que el service llamo a las dependencias!
        verify(userRepository, times(1)).findByEmail(email);
        verify(cartRepository, times(1)).findByUser(testUser);

    }

    @Test
    void getCart_deberiaDarError_cuadoUsuarioNoExiste() {

        // Arrange
        String email = "No existe";

        // Si el usuario no existe, devuelve Optional vacio
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert

        // Como no existe el usuario, deberia dar error
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,() -> {
            cartService.getCart(email);
        });

        assertTrue(ex.getMessage().contains(email)); // el mensaje menciona el email que falló

    }

    // ============ Pruebas metodo addCartItem() =============
    @Test
    void addCartItem_deberiaRetornarCarrito_cuandoProductoExiste() {

        // Arrange

        // Creo un producto cualquiera
        Product product = new Product();

        // Creo el CartItem
        CartItem cartItem = new CartItem();
        cartItem.setCart(testCart);
        cartItem.setProduct(product);


        CartItemRequestDto requestDto = new CartItemRequestDto();
        requestDto.setProductId(product.getId());
        requestDto.setQuantity(10); // Cantidad del producto

        // Le digo al mock, lo que me tiene que devolver
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartRepository.save(testCart)).thenReturn(testCart);

        // Act
        CartResponseDto result = cartService.addCartItem(email,requestDto);

        // Assert
        assertEquals(1, result.getItems().size());

    }

    @Test
    void addCartItem_deberiaDarError_cuandoProductoNoExiste() {

        // Arrange

        CartItemRequestDto requestDto = new CartItemRequestDto();

        // Le digo al mock, lo que me tiene que devolver
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(requestDto.getProductId())).thenReturn(Optional.empty()); // Si el producto no existe, devuelve null


        // Act & Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {

            cartService.addCartItem(email,requestDto);
        });


    }

    @Test
    void addCartItem_deberiaSumarCantidad_cuandoProductoExisteEnCarrito() {

        // Arrange

        // Creo producto que ya esta en el carrito.
        Product product = new Product();
        product.setId(1L);

        // El item que ya existe en el carrito
        CartItem existingItem = new CartItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(2);  // cantidad inicial

        // El carrito ya tiene ese item
        testCart.getItems().add(existingItem);

        CartItemRequestDto request = new CartItemRequestDto();
        request.setProductId(1L);  // mismo ID
        request.setQuantity(3);    // quiere agregar 3 más

        // Le digo al mock, lo que me tiene que devolver
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartRepository.save(testCart)).thenReturn(testCart);

        // Act
        cartService.addCartItem(email ,request);

        // Assert
        assertEquals(5, existingItem.getQuantity());

    }

    // ============= Pruebas metodo updateCartQuantity() ===========
    @Test
    void updateCartQuantity_deberiaDarError_cuandoUsuarioNoExiste() {

        // Arrange
        String email = "No existe";

        // Le digo al mock, lo que me tiene que devolver
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert

        // Como no existe el usuario, deberia dar error
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,() -> {
            cartService.updateCartQuantity(email,1L, 4);
        });

        assertTrue(ex.getMessage().contains(email)); // el mensaje menciona el email que falló
    }

    @Test
    void updateCartQuantity_debeDarError_cuandoProductoNoEstaEnCarrito() {

        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {

            cartService.updateCartQuantity(email, 99L, 2);
        });

    }

    @Test
    void updateCartQuantity_debeDarError_cuandoCantidadEsMayorAStock() {

        // Arrange

        // Creo un producto y lo agrego al carrito.
        Product product = new Product();
        product.setStock(10);

        // Agrego el cartItem al carrito
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        testCart.getItems().add(cartItem);

        // Le digo al mock, lo que me tiene que devolver
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {

            cartService.updateCartQuantity(email,1L,15);
        });


    }

    @Test
    void updateCartQuantity_debeActualizarCantidad() {

        // Arrange

        // Creo un producto y lo agrego al carrito.
        Product product = new Product();
        product.setStock(10);

        // Agrego el cartItem al carrito
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        testCart.getItems().add(cartItem);

        // Le digo al mock, lo que me tiene que devolver
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(testCart)).thenReturn(testCart);

        // Act
        cartService.updateCartQuantity(email,1L,7);

        // Assert
        assertEquals(7, cartItem.getQuantity());

    }

    // ============= Pruebas metodo removeCartItem() ==============
    @Test
    void removeCartItem_deberiaDarError_cuandoUsuarioNoExiste() {

        // Arrange

        // Le digo al mock, lo que me tiene que devolver
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {

            cartService.removeCartItem(email, 1L);

        });
    }

    @Test
    void removeCartItem_deberiaEliminarCartItem() {

        // Arrange

        // Agrego el cartItem al carrito
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        testCart.getItems().add(cartItem);

        // Le digo al mock, lo que me tiene que devolver
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(testCart)).thenReturn(testCart);

        // Act
        cartService.removeCartItem(email, 1L);

        // Assert
        assertEquals(0, testCart.getItems().size());
        assertFalse(testCart.getItems().contains(cartItem));

    }

    @Test
    void removeCartItem_deberiaDarError_cuandoCartItemNoExiste() {

        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {

           cartService.removeCartItem(email, 1L);

        });
    }

    // =================== Pruebas metodo deleteCart() ================

    @Test
    void deleteCart_deberiaLimpiarCarrito() {

        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(testCart)).thenReturn(testCart);

        // Act
        cartService.deleteCart(email);

        // Assert
        assertEquals(0, testCart.getItems().size());

    }
}