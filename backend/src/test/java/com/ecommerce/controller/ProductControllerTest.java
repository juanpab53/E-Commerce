package com.ecommerce.controller;

import com.ecommerce.dto.ProductDTO;
import com.ecommerce.dto.ProductResponseDTO;
import com.ecommerce.service.ProductService;
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
        "spring.datasource.url=jdbc:h2:mem:testdb_prod",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "supabase.url=http://localhost:54321",
        "supabase.key=testkeyfake"
})
public class ProductControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("POST /products - Create product successfully")
    void createProduct() throws Exception {
        ProductDTO dto = new ProductDTO("Laptop", "Gamer", 1500.0, 10, 1L);
        ProductResponseDTO res = new ProductResponseDTO(1L, "Laptop", "Gamer", 1500.0, 10, "Electrónica");

        when(productService.create(any(ProductDTO.class))).thenReturn(res);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated()) // Expected 201
                .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    @DisplayName("GET /products - List all")
    void listAll() throws Exception {
        when(productService.listAll()).thenReturn(List.of());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /products/category/{categoryId} - List by category")
    void listByCategory() throws Exception {
        when(productService.listByCategory(1L)).thenReturn(List.of());

        mockMvc.perform(get("/products/category/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /products/{id} - Find by ID")
    void findById() throws Exception {
        ProductResponseDTO res = new ProductResponseDTO(1L, "Laptop", "Gamer", 1500.0, 10, "Electrónica");
        when(productService.findById(1L)).thenReturn(res);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /products/search - Search by name")
    void findByName() throws Exception {
        when(productService.findByName("Laptop")).thenReturn(List.of());

        mockMvc.perform(get("/products/search")
                .param("name", "Laptop"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /products/{id}/stock - Update stock")
    void updateStock() throws Exception {
        ProductResponseDTO res = new ProductResponseDTO(1L, "Laptop", "Gamer", 1500.0, 20, "Electrónica");
        when(productService.updateStock(anyLong(), anyInt())).thenReturn(res);

        mockMvc.perform(patch("/products/1/stock") //
                .param("quantity", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(20));
    }

    @Test
    @DisplayName("DELETE /products/{id} - Delete successfully")
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent()); //
    }
}
