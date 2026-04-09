package com.restaurant.controller;

import com.restaurant.dto.request.CreateOrderRequest;
import com.restaurant.dto.response.OrderResponse;
import com.restaurant.service.QueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final QueueService queueService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse place(@RequestBody @Valid CreateOrderRequest request) {
        var dishQuantities = request.items().stream()
                .collect(Collectors.toMap(i -> i.dishId(), i -> i.quantity()));
        return OrderResponse.from(queueService.placeOrder(request.customerName(), dishQuantities));
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable Long id) {
        return OrderResponse.from(queueService.findById(id));
    }

    @PatchMapping("/{id}/status")
    public OrderResponse advanceStatus(@PathVariable Long id) {
        return OrderResponse.from(queueService.advanceStatus(id));
    }
}
