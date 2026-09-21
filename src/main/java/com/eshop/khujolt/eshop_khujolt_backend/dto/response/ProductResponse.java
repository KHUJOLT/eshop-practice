package com.eshop.khujolt.eshop_khujolt_backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(Long id, String name, String description, BigDecimal price, Integer stock, LocalDateTime createdAt) {


}
