package com.eshop.khujolt.eshop_khujolt_backend.repository;

import com.eshop.khujolt.eshop_khujolt_backend.entity.Cart;
import com.eshop.khujolt.eshop_khujolt_backend.entity.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @EntityGraph(attributePaths = "product")
    List<CartItem> findByCartIdOrderByIdAsc(Long cartId);

    @EntityGraph(attributePaths = "product")
    Optional<CartItem> findByCartIdAndProductId(
            Long cartId,
            Long productId
    );

    Long cart(Cart cart);
}
