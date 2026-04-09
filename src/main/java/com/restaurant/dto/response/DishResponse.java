package com.restaurant.dto.response;

import com.restaurant.domain.Dish;

import java.math.BigDecimal;

public record DishResponse(
        Long id,
        String name,
        BigDecimal price,
        BigDecimal prepTimeMinutes
) {
    public static DishResponse from(Dish dish) {
        return new DishResponse(dish.getId(), dish.getName(), dish.getPrice(), dish.getPrepTimeMinutes());
    }
}
