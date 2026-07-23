package com.micompany.ecommerce.services.cart;

import com.micompany.ecommerce.dto.carts.CartItemRequestDto;
import com.micompany.ecommerce.dto.carts.CartResponseDto;
import com.micompany.ecommerce.exceptions.InsufficientStockException;
import com.micompany.ecommerce.exceptions.InvalidQuantityException;
import com.micompany.ecommerce.exceptions.ResourceNotFoundException;
import com.micompany.ecommerce.models.entities.Cart;
import com.micompany.ecommerce.models.entities.CartItem;
import com.micompany.ecommerce.models.entities.Product;
import com.micompany.ecommerce.models.entities.User;
import com.micompany.ecommerce.repositories.CartRepository;
import com.micompany.ecommerce.repositories.ProductRepository;
import com.micompany.ecommerce.repositories.UserRepository;
import com.micompany.ecommerce.services.carts.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private String email;
    private User testUser;
    private Cart testCart;
    private Product testProduct;

    @BeforeEach
    void setUp() {

        email = "example@gmail.com";

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail(email);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Notebook");
        testProduct.setPrice(1000.0);
        testProduct.setStock(10);
    }

    @Test
    void getCart_debeRetornarCarrito_cuandoUsuarioExiste() {

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser(testUser))
                .thenReturn(Optional.of(testCart));

        CartResponseDto result = cartService.getCart(email);

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());
        assertEquals(0.0, result.getTotal());

        verify(userRepository).findByEmail(email);
        verify(cartRepository).findByUser(testUser);
    }

    @Test
    void getCart_debeDarError_cuandoUsuarioNoExiste() {

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.getCart(email)
        );

        assertEquals("User", exception.getResourceName());
        assertEquals("email", exception.getFieldName());
        assertEquals(email, exception.getFieldValue());
    }

    @Test
    void addCartItem_debeAgregarProducto_cuandoDatosSonValidos() {

        CartItemRequestDto request = new CartItemRequestDto();
        request.setProductId(testProduct.getId());
        request.setQuantity(2);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser(testUser))
                .thenReturn(Optional.of(testCart));

        when(productRepository.findById(testProduct.getId()))
                .thenReturn(Optional.of(testProduct));

        when(cartRepository.save(testCart))
                .thenReturn(testCart);

        CartResponseDto result =
                cartService.addCartItem(email, request);

        assertEquals(1, result.getItems().size());
        assertEquals(2, testCart.getItems().get(0).getQuantity());
    }

    @Test
    void addCartItem_debeDarError_cuandoCantidadEsInvalida() {

        CartItemRequestDto request = new CartItemRequestDto();
        request.setProductId(1L);
        request.setQuantity(0);

        assertThrows(
                InvalidQuantityException.class,
                () -> cartService.addCartItem(email, request)
        );

        // La validación sucede antes de acceder a la base.
        verifyNoInteractions(
                userRepository,
                productRepository,
                cartRepository
        );
    }

    @Test
    void addCartItem_debeDarError_cuandoProductoNoExiste() {

        CartItemRequestDto request = new CartItemRequestDto();
        request.setProductId(99L);
        request.setQuantity(1);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser(testUser))
                .thenReturn(Optional.of(testCart));

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.addCartItem(email, request)
        );

        assertEquals("Product", exception.getResourceName());
        assertEquals(99L, exception.getFieldValue());
    }

    @Test
    void addCartItem_debeSumarCantidad_cuandoProductoYaExiste() {

        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCart(testCart);
        existingItem.setProduct(testProduct);
        existingItem.setQuantity(2);

        testCart.getItems().add(existingItem);

        CartItemRequestDto request = new CartItemRequestDto();
        request.setProductId(testProduct.getId());
        request.setQuantity(3);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser(testUser))
                .thenReturn(Optional.of(testCart));

        when(productRepository.findById(testProduct.getId()))
                .thenReturn(Optional.of(testProduct));

        when(cartRepository.save(testCart))
                .thenReturn(testCart);

        cartService.addCartItem(email, request);

        assertEquals(5, existingItem.getQuantity());
    }

    @Test
    void addCartItem_debeDarError_cuandoCantidadFinalSuperaStock() {

        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCart(testCart);
        existingItem.setProduct(testProduct);
        existingItem.setQuantity(8);

        testCart.getItems().add(existingItem);

        CartItemRequestDto request = new CartItemRequestDto();
        request.setProductId(testProduct.getId());
        request.setQuantity(3);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser(testUser))
                .thenReturn(Optional.of(testCart));

        when(productRepository.findById(testProduct.getId()))
                .thenReturn(Optional.of(testProduct));

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> cartService.addCartItem(email, request)
        );

        assertEquals(11, exception.getRequestedQuantity());
        assertEquals(10, exception.getAvailableStock());

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateCartQuantity_debeDarError_cuandoCantidadEsInvalida() {

        assertThrows(
                InvalidQuantityException.class,
                () -> cartService.updateCartQuantity(
                        email,
                        1L,
                        -1
                )
        );

        verifyNoInteractions(
                userRepository,
                productRepository,
                cartRepository
        );
    }

    @Test
    void updateCartQuantity_debeDarError_cuandoUsuarioNoExiste() {

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.updateCartQuantity(
                        email,
                        1L,
                        4
                )
        );
    }

    @Test
    void updateCartQuantity_debeDarError_cuandoItemNoExiste() {

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser(testUser))
                .thenReturn(Optional.of(testCart));

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.updateCartQuantity(
                        email,
                        99L,
                        2
                )
        );

        assertEquals("Item", exception.getResourceName());
        assertEquals(99L, exception.getFieldValue());
    }

    @Test
    void updateCartQuantity_debeDarError_cuandoSuperaStock() {

        CartItem item = new CartItem();
        item.setId(1L);
        item.setCart(testCart);
        item.setProduct(testProduct);
        item.setQuantity(1);

        testCart.getItems().add(item);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser(testUser))
                .thenReturn(Optional.of(testCart));

        InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> cartService.updateCartQuantity(
                        email,
                        1L,
                        15
                )
        );

        assertEquals(testProduct.getId(), exception.getProductId());
        assertEquals(15, exception.getRequestedQuantity());
        assertEquals(10, exception.getAvailableStock());

        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateCartQuantity_debeActualizarCantidad() {

        CartItem item = new CartItem();
        item.setId(1L);
        item.setCart(testCart);
        item.setProduct(testProduct);
        item.setQuantity(1);

        testCart.getItems().add(item);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser(testUser))
                .thenReturn(Optional.of(testCart));

        when(cartRepository.save(testCart))
                .thenReturn(testCart);

        CartResponseDto result =
                cartService.updateCartQuantity(
                        email,
                        1L,
                        7
                );

        assertNotNull(result);
        assertEquals(7, item.getQuantity());
    }

    @Test
    void removeCartItem_debeDarError_cuandoUsuarioNoExiste() {

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.removeCartItem(email, 1L)
        );
    }

    @Test
    void removeCartItem_debeDarError_cuandoItemNoExiste() {

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser(testUser))
                .thenReturn(Optional.of(testCart));

        assertThrows(
                ResourceNotFoundException.class,
                () -> cartService.removeCartItem(email, 99L)
        );
    }

    @Test
    void removeCartItem_debeEliminarItem() {

        CartItem item = new CartItem();
        item.setId(1L);
        item.setCart(testCart);
        item.setProduct(testProduct);
        item.setQuantity(1);

        testCart.getItems().add(item);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser(testUser))
                .thenReturn(Optional.of(testCart));

        when(cartRepository.save(testCart))
                .thenReturn(testCart);

        CartResponseDto result =
                cartService.removeCartItem(email, 1L);

        assertNotNull(result);
        assertTrue(testCart.getItems().isEmpty());
    }

    @Test
    void deleteCart_debeLimpiarCarrito() {

        CartItem item = new CartItem();
        item.setId(1L);
        item.setCart(testCart);
        item.setProduct(testProduct);
        item.setQuantity(1);

        testCart.getItems().add(item);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser(testUser))
                .thenReturn(Optional.of(testCart));

        when(cartRepository.save(testCart))
                .thenReturn(testCart);

        cartService.deleteCart(email);

        assertTrue(testCart.getItems().isEmpty());
        verify(cartRepository).save(testCart);
    }
}