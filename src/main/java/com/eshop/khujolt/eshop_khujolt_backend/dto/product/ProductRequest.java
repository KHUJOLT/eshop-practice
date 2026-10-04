package com.eshop.khujolt.eshop_khujolt_backend.dto.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "Product is required!")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String name,

        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        String description,

        @NotNull(message = "Price is required!")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0!")
        @Digits(integer = 8, fraction = 2, message = "Price must have only two digits after \".\"")
        BigDecimal price,

        @NotNull(message = "Stock is required")
        @Min(value = 0, message = "Stock cannot be negative")
        Integer stock,

        @NotNull(message = "Category is required")
        @Positive
        Long categoryId
) {
}
