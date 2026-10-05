package com.eshop.khujolt.eshop_khujolt_backend.repository;

import com.eshop.khujolt.eshop_khujolt_backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long id);
}
