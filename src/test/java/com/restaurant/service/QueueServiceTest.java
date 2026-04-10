package com.restaurant.service;

import com.restaurant.domain.Dish;
import com.restaurant.domain.Order;
import com.restaurant.domain.OrderItem;
import com.restaurant.domain.OrderStatus;
import com.restaurant.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DishService dishService;

    @InjectMocks
    private QueueService queueService;

    @Test
    void placeOrder_savesOrderWithCorrectItems() {
        var dish = dish(1L, "Pizza", "20");
        when(dishService.findById(1L)).thenReturn(dish);
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = queueService.placeOrder("Alice", Map.of(1L, new BigDecimal("2")));

        assertThat(result.getCustomerName()).isEqualTo("Alice");
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getQuantity()).isEqualByComparingTo("2");
        assertThat(result.getItems().get(0).getDish()).isEqualTo(dish);
    }

    @Test
    void advanceStatus_pendingToInProgress() {
        var order = order(1L, OrderStatus.PENDING);
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        var result = queueService.advanceStatus(1L);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
    }

    @Test
    void advanceStatus_inProgressToReady() {
        var order = order(1L, OrderStatus.IN_PROGRESS);
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        var result = queueService.advanceStatus(1L);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.READY);
    }

    @Test
    void advanceStatus_throwsWhenAlreadyDelivered() {
        var order = order(1L, OrderStatus.DELIVERED);
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> queueService.advanceStatus(1L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void estimateWait_forFirstOrder_returnsItsOwnPrepTime() {
        var dish = dish(1L, "Pizza", "20");
        var order = orderWithItem(1L, OrderStatus.PENDING, dish, "1");

        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(orderRepository.findByStatusInOrderByCreatedAtAsc(anyList())).thenReturn(List.of(order));

        var result = queueService.estimateWait(1L);

        assertThat(result).isEqualByComparingTo("20");
    }

    @Test
    void estimateWait_forSecondOrder_includesPrecedingOrdersPrepTime() {
        var dish = dish(1L, "Pizza", "20");
        var order1 = orderWithItem(1L, OrderStatus.IN_PROGRESS, dish, "1");
        var order2 = orderWithItem(2L, OrderStatus.PENDING, dish, "2");

        when(orderRepository.findByIdWithItems(2L)).thenReturn(Optional.of(order2));
        when(orderRepository.findByStatusInOrderByCreatedAtAsc(anyList())).thenReturn(List.of(order1, order2));

        var result = queueService.estimateWait(2L);

        assertThat(result).isEqualByComparingTo("60");
    }

    @Test
    void estimateWait_multipleItemsPerOrder_sumsAllPrepTimes() {
        var pizza = dish(1L, "Pizza", "20");
        var burger = dish(2L, "Burger", "10");

        var order = order(1L, OrderStatus.PENDING);
        order.getItems().add(item(order, pizza, "1"));
        order.getItems().add(item(order, burger, "3"));

        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(orderRepository.findByStatusInOrderByCreatedAtAsc(anyList())).thenReturn(List.of(order));

        var result = queueService.estimateWait(1L);

        assertThat(result).isEqualByComparingTo("50");
    }

    private Dish dish(Long id, String name, String prepTime) {
        return Dish.builder()
                .id(id)
                .name(name)
                .price(new BigDecimal("10.00"))
                .prepTimeMinutes(new BigDecimal(prepTime))
                .build();
    }

    private Order order(Long id, OrderStatus status) {
        return Order.builder()
                .id(id)
                .customerName("Customer")
                .status(status)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
    }

    private Order orderWithItem(Long id, OrderStatus status, Dish dish, String quantity) {
        var order = order(id, status);
        order.getItems().add(item(order, dish, quantity));
        return order;
    }

    private OrderItem item(Order order, Dish dish, String quantity) {
        return OrderItem.builder()
                .order(order)
                .dish(dish)
                .quantity(new BigDecimal(quantity))
                .build();
    }
}
