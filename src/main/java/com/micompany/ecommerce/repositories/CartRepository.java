package com.micompany.ecommerce.repositories;

import com.micompany.ecommerce.models.entities.Cart;
import com.micompany.ecommerce.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
}
