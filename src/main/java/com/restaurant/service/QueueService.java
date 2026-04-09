package com.restaurant.service;

import com.restaurant.domain.Dish;
import com.restaurant.domain.Order;
import com.restaurant.domain.OrderItem;
import com.restaurant.domain.OrderStatus;
import com.restaurant.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QueueService {

    private static final List<OrderStatus> ACTIVE_STATUSES = List.of(OrderStatus.PENDING, OrderStatus.IN_PROGRESS);

    private final OrderRepository orderRepository;
    private final DishService dishService;

    @Transactional
    public Order placeOrder(String customerName, Map<Long, BigDecimal> dishQuantities) {
        Order order = Order.builder()
                .customerName(customerName)
                .build();

        List<OrderItem> items = dishQuantities.entrySet().stream()
                .map(entry -> {
                    Dish dish = dishService.findById(entry.getKey());
                    return OrderItem.builder()
                            .order(order)
                            .dish(dish)
                            .quantity(entry.getValue())
                            .build();
                })
                .toList();

        order.getItems().addAll(items);
        return orderRepository.save(order);
    }

    @Transactional
    public Order advanceStatus(Long orderId) {
        Order order = findById(orderId);
        OrderStatus next = switch (order.getStatus()) {
            case PENDING -> OrderStatus.IN_PROGRESS;
            case IN_PROGRESS -> OrderStatus.READY;
            case READY -> OrderStatus.DELIVERED;
            case DELIVERED -> throw new IllegalStateException("Order already delivered");
        };
        order.setStatus(next);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public BigDecimal estimateWait(Long orderId) {
        Order target = findById(orderId);
        List<Order> activeOrders = orderRepository.findByStatusInOrderByCreatedAtAsc(ACTIVE_STATUSES);

        BigDecimal total = BigDecimal.ZERO;
        for (Order o : activeOrders) {
            total = total.add(calculatePrepTime(o));
            if (o.getId().equals(target.getId())) {
                break;
            }
        }
        return total;
    }

    @Transactional(readOnly = true)
    public List<Order> getActiveQueue() {
        return orderRepository.findByStatusInOrderByCreatedAtAsc(ACTIVE_STATUSES);
    }

    @Transactional(readOnly = true)
    public Order findById(Long orderId) {
        return orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));
    }

    private BigDecimal calculatePrepTime(Order order) {
        return order.getItems().stream()
                .map(item -> item.getDish().getPrepTimeMinutes().multiply(item.getQuantity()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
