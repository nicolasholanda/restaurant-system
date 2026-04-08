package com.restaurant.repository;

import com.restaurant.domain.Order;
import com.restaurant.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatusInOrderByCreatedAtAsc(List<OrderStatus> statuses);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.dish WHERE o.id = :id")
    java.util.Optional<Order> findByIdWithItems(Long id);
}
