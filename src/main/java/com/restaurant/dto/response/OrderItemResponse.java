package com.restaurant.dto.response;

import com.restaurant.domain.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long dishId,
        String dishName,
        BigDecimal quantity,
        BigDecimal prepTimeMinutes
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getDish().getId(),
                item.getDish().getName(),
                item.getQuantity(),
                item.getDish().getPrepTimeMinutes()
        );
    }
}
