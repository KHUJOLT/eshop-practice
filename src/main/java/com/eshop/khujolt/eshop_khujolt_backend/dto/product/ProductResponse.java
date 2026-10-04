package com.eshop.khujolt.eshop_khujolt_backend.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        LocalDateTime createdAt,
        Long categoryId,
        String categoryName
) {
}
