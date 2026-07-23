package com.micompany.ecommerce.services.orders;

import com.micompany.ecommerce.dto.orders.OrderResponseDto;
import com.micompany.ecommerce.dto.orders.OrderStatusUpdateDto;
import com.micompany.ecommerce.exceptions.EmptyCartException;
import com.micompany.ecommerce.exceptions.ResourceNotFoundException;
import com.micompany.ecommerce.mappers.Mapper;
import com.micompany.ecommerce.models.entities.Cart;
import com.micompany.ecommerce.models.entities.Order;
import com.micompany.ecommerce.models.entities.User;
import com.micompany.ecommerce.models.enums.Rol;
import com.micompany.ecommerce.models.enums.Status;
import com.micompany.ecommerce.repositories.CartRepository;
import com.micompany.ecommerce.repositories.OrderRepository;
import com.micompany.ecommerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService implements IOrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public OrderResponseDto createOrder(String userEmail) {

        Cart cart = getCart(userEmail);

        Order order = Mapper.cartToOrder(cart);
        orderRepository.save(order);

        // Limpiar cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return Mapper.orderToResponseDTO(order);
    }

    @Override
    public List<OrderResponseDto> getMyOrders(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        return orderRepository.findAllByUser(user).stream()
                .map(Mapper::orderToResponseDTO).toList();
    }

    @Override
    public OrderResponseDto getOrder(String userEmail, Long orderId) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        if(user.getRol()==Rol.ADMIN) {
            return Mapper.orderToResponseDTO(
                    orderRepository.findById(orderId)
                            .orElseThrow(() -> new ResourceNotFoundException("Order","id", orderId)));
        }

        List<Order> ordenList = orderRepository.findAllByUser(user);

        return ordenList.stream()
                .filter(order -> order.getId().equals(orderId))
                .map(Mapper::orderToResponseDTO)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Order","id",orderId));
    }

    @Override
    public List<OrderResponseDto> getAllOrders(Status statusFilter) {

        if(statusFilter != null) {
            return orderRepository.findAll().stream()
                    .filter(or -> or.getStatus().name().equals(statusFilter.name()))
                    .map(Mapper::orderToResponseDTO).toList();
        }

        return orderRepository.findAll().stream()
                .map(Mapper::orderToResponseDTO).toList();

    }

    @Override
    public OrderResponseDto updateOrderState(Long id, OrderStatusUpdateDto request) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order","id",id));

        order.setStatus(request.getStatus());

        return Mapper.orderToResponseDTO(orderRepository.save(order));
    }

    // ====================================================================
    // MÉTODOS PRIVADOS DE APOYO (Helpers)
    // ====================================================================

    private Cart getCart (String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "userEmail", userEmail));

        if(cart.getItems().isEmpty()) {
            throw new EmptyCartException();
        }

        return cart;
    }

}
