package com.restaurant.controller;

import com.restaurant.domain.Dish;
import com.restaurant.domain.Order;
import com.restaurant.domain.OrderItem;
import com.restaurant.domain.OrderStatus;
import com.restaurant.service.QueueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QueueController.class)
class QueueControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueueService queueService;

    @Test
    void getActiveQueue_returns200WithOrderList() throws Exception {
        when(queueService.getActiveQueue()).thenReturn(List.of(order(1L, OrderStatus.PENDING), order(2L, OrderStatus.IN_PROGRESS)));

        mockMvc.perform(get("/queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"));
    }

    @Test
    void getActiveQueue_returns200WithEmptyList() throws Exception {
        when(queueService.getActiveQueue()).thenReturn(List.of());

        mockMvc.perform(get("/queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getEstimate_returns200WithWaitTime() throws Exception {
        var order = order(1L, OrderStatus.PENDING);
        when(queueService.findById(1L)).thenReturn(order);
        when(queueService.estimateWait(1L)).thenReturn(new BigDecimal("40"));

        mockMvc.perform(get("/queue/1/estimate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.customerName").value("Alice"))
                .andExpect(jsonPath("$.estimatedWaitMinutes").value(40));
    }

    private Order order(Long id, OrderStatus status) {
        var dish = Dish.builder()
                .id(1L)
                .name("Pizza")
                .price(new BigDecimal("15.00"))
                .prepTimeMinutes(new BigDecimal("20"))
                .build();
        var order = Order.builder()
                .id(id)
                .customerName("Alice")
                .status(status)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        order.getItems().add(OrderItem.builder().id(1L).order(order).dish(dish).quantity(new BigDecimal("2")).build());
        return order;
    }
}
