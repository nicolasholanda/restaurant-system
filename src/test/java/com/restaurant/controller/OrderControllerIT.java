package com.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.domain.Dish;
import com.restaurant.domain.Order;
import com.restaurant.domain.OrderItem;
import com.restaurant.domain.OrderStatus;
import com.restaurant.dto.request.CreateOrderRequest;
import com.restaurant.dto.request.OrderItemRequest;
import com.restaurant.service.QueueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QueueService queueService;

    @Test
    void place_returns201WithOrder() throws Exception {
        var request = new CreateOrderRequest("Alice", List.of(new OrderItemRequest(1L, new BigDecimal("2"))));
        var order = order(1L, OrderStatus.PENDING);
        when(queueService.placeOrder(eq("Alice"), anyMap())).thenReturn(order);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void place_returns400WhenItemsAreEmpty() throws Exception {
        var request = new CreateOrderRequest("Alice", List.of());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void findById_returns200WithOrder() throws Exception {
        var order = order(1L, OrderStatus.PENDING);
        when(queueService.findById(1L)).thenReturn(order);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.customerName").value("Alice"));
    }

    @Test
    void advanceStatus_returns200WithUpdatedStatus() throws Exception {
        var order = order(1L, OrderStatus.IN_PROGRESS);
        when(queueService.advanceStatus(1L)).thenReturn(order);

        mockMvc.perform(patch("/orders/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
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
