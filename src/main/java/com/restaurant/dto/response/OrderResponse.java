package com.restaurant.dto.response;

import com.restaurant.domain.Order;
import com.restaurant.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerName,
        OrderStatus status,
        LocalDateTime createdAt,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getItems().stream().map(OrderItemResponse::from).toList()
        );
    }
}
