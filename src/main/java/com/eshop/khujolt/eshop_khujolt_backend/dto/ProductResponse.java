package com.eshop.khujolt.eshop_khujolt_backend.dto;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, BigDecimal price, Integer stock) {


}
