package com.ecommerce.controller;

import com.ecommerce.dto.CategoryDTO;
import com.ecommerce.dto.CategoryResponseDTO;
import com.ecommerce.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb_cat",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "supabase.url=http://localhost:54321",
    "supabase.key=testkeyfake"
})
public class CategoryControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private CategoryService categoryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("GET /categories - List all categories")
    void listCategories() throws Exception {
        when(categoryService.listAll()).thenReturn(List.of(new CategoryResponseDTO(1L, "Electrónica")));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Electrónica"));
    }

    @Test
    @DisplayName("GET /categories/{id} - Find category by ID")
    void findById() throws Exception {
        CategoryResponseDTO response = new CategoryResponseDTO(1L, "Ropa");
        when(categoryService.findById(1L)).thenReturn(response);

        mockMvc.perform(get("/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ropa"));
    }

    @Test
    @DisplayName("POST /categories - Create category successfully")
    void createCategory() throws Exception {
        CategoryDTO registration = new CategoryDTO("Hogar");
        CategoryResponseDTO response = new CategoryResponseDTO(1L, "Hogar");

        when(categoryService.create(any(CategoryDTO.class))).thenReturn(response);

        mockMvc.perform(post("/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isCreated()) 
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Hogar"));
    }

    @Test
    @DisplayName("PUT /categories/{id} - Update category")
    void updateCategory() throws Exception {
        CategoryDTO update = new CategoryDTO("Hogar Modificado");
        CategoryResponseDTO response = new CategoryResponseDTO(1L, "Hogar Modificado");

        when(categoryService.update(anyLong(), any(CategoryDTO.class))).thenReturn(response);

        mockMvc.perform(put("/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hogar Modificado"));
    }

    @Test
    @DisplayName("DELETE /categories/{id} - Delete category")
    void deleteCategory() throws Exception {
        mockMvc.perform(delete("/categories/1"))
                .andExpect(status().isNoContent()); 
    }
}
