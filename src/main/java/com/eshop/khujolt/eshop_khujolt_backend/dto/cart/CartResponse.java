package com.eshop.khujolt.eshop_khujolt_backend.dto.cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        List<CartItemResponse> items,
        long totalQuantity,
        BigDecimal total
) {
}
