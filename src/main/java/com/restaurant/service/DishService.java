package com.restaurant.service;

import com.restaurant.domain.Dish;
import com.restaurant.repository.DishRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishService {

    private final DishRepository dishRepository;

    @Transactional(readOnly = true)
    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Dish findById(Long id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dish not found: " + id));
    }

    @Transactional
    public Dish create(Dish dish) {
        return dishRepository.save(dish);
    }

    @Transactional
    public Dish update(Long id, Dish updated) {
        Dish dish = findById(id);
        dish.setName(updated.getName());
        dish.setPrice(updated.getPrice());
        dish.setPrepTimeMinutes(updated.getPrepTimeMinutes());
        return dishRepository.save(dish);
    }

    @Transactional
    public void delete(Long id) {
        dishRepository.delete(findById(id));
    }
}
