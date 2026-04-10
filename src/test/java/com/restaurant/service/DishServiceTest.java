package com.restaurant.service;

import com.restaurant.domain.Dish;
import com.restaurant.repository.DishRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DishServiceTest {

    @Mock
    private DishRepository dishRepository;

    @InjectMocks
    private DishService dishService;

    @Test
    void findAll_returnsAllDishes() {
        var dish = dish(1L, "Pizza", "15.00", "20");
        when(dishRepository.findAll()).thenReturn(List.of(dish));

        var result = dishService.findAll();

        assertThat(result).hasSize(1).containsExactly(dish);
    }

    @Test
    void findById_returnsDish_whenFound() {
        var dish = dish(1L, "Pizza", "15.00", "20");
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));

        var result = dishService.findById(1L);

        assertThat(result).isEqualTo(dish);
    }

    @Test
    void findById_throwsEntityNotFoundException_whenNotFound() {
        when(dishRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dishService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsDish() {
        var dish = dish(null, "Burger", "12.00", "15");
        var saved = dish(1L, "Burger", "12.00", "15");
        when(dishRepository.save(dish)).thenReturn(saved);

        var result = dishService.create(dish);

        assertThat(result.getId()).isEqualTo(1L);
        verify(dishRepository).save(dish);
    }

    @Test
    void update_updatesFieldsAndSaves() {
        var existing = dish(1L, "Pizza", "15.00", "20");
        var updated = dish(null, "Pizza XL", "18.00", "25");
        when(dishRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(dishRepository.save(existing)).thenReturn(existing);

        var result = dishService.update(1L, updated);

        assertThat(result.getName()).isEqualTo("Pizza XL");
        assertThat(result.getPrice()).isEqualByComparingTo("18.00");
        assertThat(result.getPrepTimeMinutes()).isEqualByComparingTo("25");
    }

    @Test
    void delete_deletesExistingDish() {
        var dish = dish(1L, "Pizza", "15.00", "20");
        when(dishRepository.findById(1L)).thenReturn(Optional.of(dish));

        dishService.delete(1L);

        verify(dishRepository).delete(dish);
    }

    private Dish dish(Long id, String name, String price, String prepTime) {
        return Dish.builder()
                .id(id)
                .name(name)
                .price(new BigDecimal(price))
                .prepTimeMinutes(new BigDecimal(prepTime))
                .build();
    }
}
