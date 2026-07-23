package com.micompany.ecommerce.services.orders;

import com.micompany.ecommerce.dto.orders.OrderResponseDto;
import com.micompany.ecommerce.dto.orders.OrderStatusUpdateDto;
import com.micompany.ecommerce.exceptions.EmptyCartException;
import com.micompany.ecommerce.exceptions.ResourceNotFoundException;
import com.micompany.ecommerce.models.entities.Cart;
import com.micompany.ecommerce.models.entities.CartItem;
import com.micompany.ecommerce.models.entities.Order;
import com.micompany.ecommerce.models.entities.Product;
import com.micompany.ecommerce.models.entities.User;
import com.micompany.ecommerce.models.enums.Rol;
import com.micompany.ecommerce.models.enums.Status;
import com.micompany.ecommerce.repositories.CartRepository;
import com.micompany.ecommerce.repositories.OrderRepository;
import com.micompany.ecommerce.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    private String email;
    private User testUser;

    @BeforeEach
    void setUp() {

        email = "example@gmail.com";

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail(email);
        testUser.setRol(Rol.CUSTOMER);
    }

    @Test
    void createOrder_debeDarError_cuandoUsuarioNoExiste() {

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.createOrder(email)
        );

        assertEquals("User", exception.getResourceName());
    }

    @Test
    void createOrder_debeDarError_cuandoCarritoNoExiste() {

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser(testUser))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.createOrder(email)
        );

        assertEquals("Cart", exception.getResourceName());
    }

    @Test
    void createOrder_debeDarError_cuandoCarritoEstaVacio() {

        Cart cart = new Cart();
        cart.setUser(testUser);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser(testUser))
                .thenReturn(Optional.of(cart));

        assertThrows(
                EmptyCartException.class,
                () -> orderService.createOrder(email)
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void createOrder_debeCrearOrden_yVaciarCarrito() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Notebook");
        product.setPrice(1000.0);

        CartItem item = new CartItem();
        item.setId(1L);
        item.setProduct(product);
        item.setQuantity(2);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(testUser);
        cart.getItems().add(item);

        item.setCart(cart);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(cartRepository.findByUser(testUser))
                .thenReturn(Optional.of(cart));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {

                    Order order = invocation.getArgument(0);
                    order.setId(1L);

                    return order;
                });

        when(cartRepository.save(cart))
                .thenReturn(cart);

        OrderResponseDto result =
                orderService.createOrder(email);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getItems().size());
        assertTrue(cart.getItems().isEmpty());

        verify(orderRepository).save(any(Order.class));
        verify(cartRepository).save(cart);
    }

    @Test
    void getMyOrders_debeDarError_cuandoUsuarioNoExiste() {

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getMyOrders(email)
        );
    }

    @Test
    void getMyOrders_debeDevolverOrdenesDelUsuario() {

        Order order1 = order(1L, Status.PENDING);
        Order order2 = order(2L, Status.CONFIRMED);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(orderRepository.findAllByUser(testUser))
                .thenReturn(List.of(order1, order2));

        List<OrderResponseDto> result =
                orderService.getMyOrders(email);

        assertEquals(2, result.size());
    }

    @Test
    void getOrder_debeDarError_cuandoUsuarioNoExiste() {

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrder(email, 99L)
        );
    }

    @Test
    void getOrder_debeDarError_cuandoOrdenNoExiste_siendoAdmin() {

        testUser.setRol(Rol.ADMIN);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(orderRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrder(email, 99L)
        );

        assertEquals("Order", exception.getResourceName());
    }

    @Test
    void getOrder_debeDarError_cuandoOrdenNoPerteneceAlCustomer() {

        testUser.setRol(Rol.CUSTOMER);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(orderRepository.findAllByUser(testUser))
                .thenReturn(Collections.emptyList());

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.getOrder(email, 99L)
        );
    }

    @Test
    void getOrder_debeDevolverOrden_siendoAdmin() {

        testUser.setRol(Rol.ADMIN);

        Order order = order(1L, Status.PENDING);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        OrderResponseDto result =
                orderService.getOrder(email, 1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getOrder_debeDevolverOrdenPropia_siendoCustomer() {

        Order order1 = order(1L, Status.PENDING);
        Order order2 = order(2L, Status.CONFIRMED);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(testUser));

        when(orderRepository.findAllByUser(testUser))
                .thenReturn(List.of(order1, order2));

        OrderResponseDto result =
                orderService.getOrder(email, 2L);

        assertEquals(2L, result.getId());
        assertEquals("CONFIRMED", result.getStatus());
    }

    @Test
    void getAllOrders_debeDevolverTodas_cuandoNoHayFiltro() {

        when(orderRepository.findAll())
                .thenReturn(
                        List.of(
                                order(1L, Status.PENDING),
                                order(2L, Status.CONFIRMED)
                        )
                );

        List<OrderResponseDto> result =
                orderService.getAllOrders(null);

        assertEquals(2, result.size());
    }

    @Test
    void getAllOrders_debeFiltrarPorEstado() {

        when(orderRepository.findAll())
                .thenReturn(
                        List.of(
                                order(1L, Status.PENDING),
                                order(2L, Status.CONFIRMED)
                        )
                );

        List<OrderResponseDto> result =
                orderService.getAllOrders(Status.PENDING);

        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
    }

    @Test
    void updateOrderState_debeDarError_cuandoOrdenNoExiste() {

        when(orderRepository.findById(99L))
                .thenReturn(Optional.empty());

        OrderStatusUpdateDto request =
                new OrderStatusUpdateDto();

        request.setStatus(Status.CANCELLED);

        assertThrows(
                ResourceNotFoundException.class,
                () -> orderService.updateOrderState(99L, request)
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void updateOrderState_debeActualizarEstado() {

        Order order = order(1L, Status.PENDING);

        OrderStatusUpdateDto request =
                new OrderStatusUpdateDto();

        request.setStatus(Status.CANCELLED);

        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        when(orderRepository.save(order))
                .thenReturn(order);

        OrderResponseDto result =
                orderService.updateOrderState(1L, request);

        assertEquals("CANCELLED", result.getStatus());
    }

    private Order order(Long id, Status status) {

        Order order = new Order();
        order.setId(id);
        order.setUser(testUser);
        order.setStatus(status);

        return order;
    }
}