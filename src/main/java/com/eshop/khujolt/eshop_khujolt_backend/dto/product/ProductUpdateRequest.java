package com.eshop.khujolt.eshop_khujolt_backend.dto.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductUpdateRequest(
        @NotBlank
        @Size(max=255)
        String name,

        @Size(max=2000)
        String description,

        @NotNull
        @DecimalMin(value="0.01")
        BigDecimal price,

        @NotNull
        @Min(value=0)
        Integer stock,

        @NotNull
        @Positive
        Long categoryId
) {
}
