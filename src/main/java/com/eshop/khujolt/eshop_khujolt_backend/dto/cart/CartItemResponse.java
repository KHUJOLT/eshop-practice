package com.eshop.khujolt.eshop_khujolt_backend.dto.cart;

import java.math.BigDecimal;

public record CartItemResponse(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal,
        Integer availableStock,
        boolean available
) {
}
