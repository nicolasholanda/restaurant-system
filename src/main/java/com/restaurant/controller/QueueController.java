package com.restaurant.controller;

import com.restaurant.dto.response.OrderResponse;
import com.restaurant.dto.response.QueueEstimateResponse;
import com.restaurant.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @GetMapping
    public List<OrderResponse> getActiveQueue() {
        return queueService.getActiveQueue().stream().map(OrderResponse::from).toList();
    }

    @GetMapping("/{orderId}/estimate")
    public QueueEstimateResponse getEstimate(@PathVariable Long orderId) {
        var order = queueService.findById(orderId);
        var wait = queueService.estimateWait(orderId);
        return new QueueEstimateResponse(order.getId(), order.getCustomerName(), wait);
    }
}
