package com.micompany.ecommerce.repositories;

import com.micompany.ecommerce.models.entities.Order;
import com.micompany.ecommerce.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

        List<Order> findAllByUser (User user);
}
