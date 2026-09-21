package com.eshop.khujolt.eshop_khujolt_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(

        @NotBlank(message = "Category is required!")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        String name

) {
}
