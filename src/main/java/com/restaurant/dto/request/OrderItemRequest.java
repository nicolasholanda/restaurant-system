package com.restaurant.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OrderItemRequest(
        @NotNull Long dishId,
        @NotNull @DecimalMin("0.1") BigDecimal quantity
) {}
