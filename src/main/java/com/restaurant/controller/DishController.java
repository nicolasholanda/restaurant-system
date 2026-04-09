package com.restaurant.controller;

import com.restaurant.domain.Dish;
import com.restaurant.dto.request.CreateDishRequest;
import com.restaurant.dto.response.DishResponse;
import com.restaurant.service.DishService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {

    private final DishService dishService;

    @GetMapping
    public List<DishResponse> findAll() {
        return dishService.findAll().stream().map(DishResponse::from).toList();
    }

    @GetMapping("/{id}")
    public DishResponse findById(@PathVariable Long id) {
        return DishResponse.from(dishService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DishResponse create(@RequestBody @Valid CreateDishRequest request) {
        Dish dish = Dish.builder()
                .name(request.name())
                .price(request.price())
                .prepTimeMinutes(request.prepTimeMinutes())
                .build();
        return DishResponse.from(dishService.create(dish));
    }

    @PutMapping("/{id}")
    public DishResponse update(@PathVariable Long id, @RequestBody @Valid CreateDishRequest request) {
        Dish updated = Dish.builder()
                .name(request.name())
                .price(request.price())
                .prepTimeMinutes(request.prepTimeMinutes())
                .build();
        return DishResponse.from(dishService.update(id, updated));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        dishService.delete(id);
    }
}
