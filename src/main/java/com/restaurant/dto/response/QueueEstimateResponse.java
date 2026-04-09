package com.restaurant.dto.response;

import java.math.BigDecimal;

public record QueueEstimateResponse(
        Long orderId,
        String customerName,
        BigDecimal estimatedWaitMinutes
) {}
