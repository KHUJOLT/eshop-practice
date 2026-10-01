package com.eshop.khujolt.eshop_khujolt_backend.dto.response;

import com.eshop.khujolt.eshop_khujolt_backend.entity.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        Role role,
        LocalDateTime createdAt
) {
}
