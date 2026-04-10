package com.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.domain.Dish;
import com.restaurant.dto.request.CreateDishRequest;
import com.restaurant.service.DishService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DishController.class)
class DishControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DishService dishService;

    @Test
    void findAll_returns200WithList() throws Exception {
        when(dishService.findAll()).thenReturn(List.of(dish(1L, "Pizza", "15.00", "20")));

        mockMvc.perform(get("/dishes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Pizza"));
    }

    @Test
    void findById_returns200WhenFound() throws Exception {
        when(dishService.findById(1L)).thenReturn(dish(1L, "Pizza", "15.00", "20"));

        mockMvc.perform(get("/dishes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pizza"));
    }

    @Test
    void findById_returns404WhenNotFound() throws Exception {
        when(dishService.findById(99L)).thenThrow(new EntityNotFoundException("Dish not found: 99"));

        mockMvc.perform(get("/dishes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Dish not found: 99"));
    }

    @Test
    void create_returns201WithCreatedDish() throws Exception {
        var request = new CreateDishRequest("Pizza", new BigDecimal("15.00"), new BigDecimal("20"));
        when(dishService.create(any())).thenReturn(dish(1L, "Pizza", "15.00", "20"));

        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Pizza"));
    }

    @Test
    void create_returns400WhenBodyIsInvalid() throws Exception {
        var request = new CreateDishRequest("", new BigDecimal("15.00"), new BigDecimal("20"));

        mockMvc.perform(post("/dishes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void delete_returns204() throws Exception {
        doNothing().when(dishService).delete(1L);

        mockMvc.perform(delete("/dishes/1"))
                .andExpect(status().isNoContent());

        verify(dishService).delete(1L);
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
