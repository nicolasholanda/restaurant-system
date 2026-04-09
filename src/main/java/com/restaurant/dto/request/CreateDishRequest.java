package com.restaurant.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateDishRequest(
        @NotBlank String name,
        @NotNull @DecimalMin("0.01") BigDecimal price,
        @NotNull @DecimalMin("0.1") BigDecimal prepTimeMinutes
) {}
