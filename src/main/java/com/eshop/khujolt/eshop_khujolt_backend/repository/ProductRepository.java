package com.eshop.khujolt.eshop_khujolt_backend.repository;

import com.eshop.khujolt.eshop_khujolt_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
