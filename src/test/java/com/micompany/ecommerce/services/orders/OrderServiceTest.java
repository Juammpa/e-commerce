package com.micompany.ecommerce.services.orders;

import com.micompany.ecommerce.dto.orders.OrderResponseDto;
import com.micompany.ecommerce.dto.orders.OrderStatusUpdateDto;
import com.micompany.ecommerce.models.entities.*;
import com.micompany.ecommerce.models.enums.Rol;
import com.micompany.ecommerce.models.enums.Status;
import com.micompany.ecommerce.repositories.CartRepository;
import com.micompany.ecommerce.repositories.OrderRepository;
import com.micompany.ecommerce.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private OrderService orderService;

    // ========== Metodos Helper =========
    private String email;
    private User testUser;

    @BeforeEach
    void setUp() {
        email = "example@gmail.com";
        testUser = new User();
        testUser.setEmail(email);
    }

    // ============= Pruebas metodo createOrder() ===============
    @Test
    void createOrder_debeDarError_cuandoUsuarioNoExiste() {

        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {

            orderService.createOrder(email);
        });
    }

    @Test
    void createOrder_debeDarError_cuandoCarritoNoExiste() {

        // Arrange

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            orderService.createOrder(email);
        });

    }

    @Test
    void createOrder_debeDarError_cuandoCarritoEstaVacio() {

        // Arrange

        Cart cart = new Cart();
        cart.setUser(testUser);
        cart.setItems(Collections.emptyList());

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(email);
        });

    }

    @Test
    void createOrder_debeCrearOrder_cuandoDatosSonCorrectos() {

        // Arrange

        // Creo producto que va en CartItem
        Product product = new Product();
        product.setId(1L);
        product.setPrice(1000d);

        // Creo CartItem que va en Cart
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);

        Cart cart = new Cart();
        cart.setUser(testUser);
        List<CartItem> items = new ArrayList<>();
        items.add(cartItem);
        cart.setItems(items);

        cartItem.setCart(cart);

        Order order = new Order();
        order.setUser(testUser);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUser(testUser)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        OrderResponseDto result = orderService.createOrder(email);

        // Assert
        assertEquals(1, result.getItems().size());
        assertTrue(cart.getItems().isEmpty());
    }

    // ================= Pruebas metodo getMyOrders() ===============
    @Test
    void getMyOrders_debeDarError_cuandoUsuarioNoExiste() {

        // Arrange
        String email = "No existe";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {

            orderService.getMyOrders(email);
        });

    }

    @Test
    void getMyOrders_debeDevolverOrdenesDelUsuario() {

        // Arrange

        // Ordenes del usuario actual
        Order order1 = new Order();
        order1.setUser(testUser);
        Order order2 = new Order();
        order2.setUser(testUser);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(orderRepository.findAllByUser(testUser)).thenReturn(List.of(order1,order2));

        // Act
        List<OrderResponseDto> result = orderService.getMyOrders(email);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ============= Pruebas metodo getOrder() ==================
    @Test
    void getOrder_debeDarError_cuandoUsuarioNoExiste() {

        // Arrange
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {

            orderService.getOrder(email, 99L);
        });

    }

    @Test
    void getOrder_debeDarError_cuandoNoExisteOrder_siendoAdmin() {

        // Arrange
        testUser.setRol(Rol.ADMIN);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
           orderService.getOrder(email, 99L);
        });

    }

    @Test
    void getOrder_debeDarError_cuandoNoExisteOrder_siendoCliente() {

        // Arrange
        testUser.setRol(Rol.CUSTOMER);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(orderRepository.findAllByUser(testUser)).thenReturn(Collections.emptyList());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            orderService.getOrder(email, 99L);
        });
    }

    @Test
    void getOrder_debeDevolverOrden_cuandoExiste_siendoAdmin() {

        // Arrange
        testUser.setRol(Rol.ADMIN);

        Order order = new Order();
        order.setId(1L);
        order.setUser(testUser);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        OrderResponseDto result = orderService.getOrder(email, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());

    }

    @Test
    void getOrder_debeDevolverListaOrdenes_siendoCliente() {

        // Arrange
        testUser.setRol(Rol.CUSTOMER);

        Order order = new Order();
        order.setId(1L);
        order.setStatus(Status.PENDING);
        order.setUser(testUser);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setStatus(Status.CONFIRMED);
        order2.setUser(testUser);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        when(orderRepository.findAllByUser(testUser)).thenReturn(List.of(order, order2));

        // Act
        OrderResponseDto result = orderService.getOrder(email, 2L);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("CONFIRMED", result.getStatus());
    }

    // =========== Pruebas metodo getAllOrders() ==============
    @Test
    void getAllOrders_debeDevolverLista_cuandoNoExisteFiltro() {

        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setUser(testUser);
        order.setStatus(Status.PENDING);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setUser(testUser);
        order2.setStatus(Status.CONFIRMED);

        when(orderRepository.findAll()).thenReturn(List.of(order, order2));

        // Act
        List<OrderResponseDto> result = orderService.getAllOrders(null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllOrders_debeDevolverLista_cuandoFiltroEsActivo() {

        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setUser(testUser);
        order.setStatus(Status.PENDING);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setUser(testUser);
        order2.setStatus(Status.CONFIRMED);

        when(orderRepository.findAll()).thenReturn(List.of(order, order2));

        // Act
        List<OrderResponseDto> result = orderService.getAllOrders(Status.PENDING);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

    }

    // ========== Pruebas metodo updateOrderState() ===============
    @Test
    void updateOrderState_debeDarError_cuandoOrderNoExiste() {

        // Arrange
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        OrderStatusUpdateDto requestDto = new OrderStatusUpdateDto();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {

           orderService.updateOrderState(99L, requestDto);
        });

    }

    @Test
    void updateOrderState_debeActualizarEstado_cuandoExiste() {

        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setUser(testUser);
        order.setStatus(Status.PENDING);

        OrderStatusUpdateDto requestDto = new OrderStatusUpdateDto();
        requestDto.setStatus(Status.CANCELLED);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderResponseDto result = orderService.updateOrderState(1L, requestDto);

        // Assert
        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());
    }
}