package com.ecommerce.controller;

import com.ecommerce.dto.ProductoDTO;
import com.ecommerce.dto.ProductoResponseDTO;
import com.ecommerce.service.ProductoService;
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
public class ProductoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private ProductoService productoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("POST /productos - Crear producto exitoso")
    void crearProducto() throws Exception {
        ProductoDTO dto = new ProductoDTO("Laptop", "Gamer", 1500.0, 10, 1L);
        ProductoResponseDTO res = new ProductoResponseDTO(1L, "Laptop", "Gamer", 1500.0, 10, "Electrónica");

        when(productoService.crear(any(ProductoDTO.class))).thenReturn(res);

        mockMvc.perform(post("/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated()) // Esperamos 201
                .andExpect(jsonPath("$.nombre").value("Laptop"));
    }

    @Test
    @DisplayName("GET /productos - Listar todos")
    void listarTodos() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /productos/categoria/{categoriaId} - Listar por categoría")
    void listarPorCategoria() throws Exception {
        when(productoService.listarPorCategoria(1L)).thenReturn(List.of());

        mockMvc.perform(get("/productos/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /productos/{id} - Buscar por ID")
    void buscarPorId() throws Exception {
        ProductoResponseDTO res = new ProductoResponseDTO(1L, "Laptop", "Gamer", 1500.0, 10, "Electrónica");
        when(productoService.buscarPorId(1L)).thenReturn(res);

        mockMvc.perform(get("/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /productos/buscar - Buscar por nombre")
    void buscarPorNombre() throws Exception {
        when(productoService.buscarPorNombre("Laptop")).thenReturn(List.of());

        mockMvc.perform(get("/productos/buscar")
                .param("nombre", "Laptop"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /productos/{id}/stock - Actualizar stock")
    void actualizarStock() throws Exception {
        ProductoResponseDTO res = new ProductoResponseDTO(1L, "Laptop", "Gamer", 1500.0, 20, "Electrónica");
        when(productoService.actualizarStock(anyLong(), anyInt())).thenReturn(res);

        mockMvc.perform(patch("/productos/1/stock") //
                .param("cantidad", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidad").value(20));
    }

    @Test
    @DisplayName("DELETE /productos/{id} - Eliminar exitoso")
    void eliminarProducto() throws Exception {
        mockMvc.perform(delete("/productos/1"))
                .andExpect(status().isNoContent()); //
    }
}