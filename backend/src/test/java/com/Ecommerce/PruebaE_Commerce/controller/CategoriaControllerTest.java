package com.Ecommerce.PruebaE_Commerce.controller;

import com.Ecommerce.PruebaE_Commerce.dto.CategoriaDTO;
import com.Ecommerce.PruebaE_Commerce.dto.CategoriaResponseDTO;
import com.Ecommerce.PruebaE_Commerce.service.CategoriaService;
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
public class CategoriaControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private CategoriaService categoriaService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("GET /categorias - Listar todas las categorías")
    void listarCategorias() throws Exception {
        when(categoriaService.listarTodas()).thenReturn(List.of(new CategoriaResponseDTO(1L, "Electrónica")));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Electrónica"));
    }

    @Test
    @DisplayName("GET /categorias/{id} - Buscar categoría por ID")
    void buscarPorId() throws Exception {
        CategoriaResponseDTO response = new CategoriaResponseDTO(1L, "Ropa");
        when(categoriaService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ropa"));
    }

    @Test
    @DisplayName("POST /categorias - Crear categoría exitosa")
    void crearCategoria() throws Exception {
        CategoriaDTO registro = new CategoriaDTO("Hogar");
        CategoriaResponseDTO response = new CategoriaResponseDTO(1L, "Hogar");

        when(categoriaService.crear(any(CategoriaDTO.class))).thenReturn(response);

        mockMvc.perform(post("/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated()) 
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Hogar"));
    }

    @Test
    @DisplayName("PUT /categorias/{id} - Actualizar categoría")
    void actualizarCategoria() throws Exception {
        CategoriaDTO actualizacion = new CategoriaDTO("Hogar Modificado");
        CategoriaResponseDTO response = new CategoriaResponseDTO(1L, "Hogar Modificado");

        when(categoriaService.actualizar(anyLong(), any(CategoriaDTO.class))).thenReturn(response);

        mockMvc.perform(put("/categorias/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizacion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Hogar Modificado"));
    }

    @Test
    @DisplayName("DELETE /categorias/{id} - Eliminar categoría")
    void eliminarCategoria() throws Exception {
        mockMvc.perform(delete("/categorias/1"))
                .andExpect(status().isNoContent()); 
    }
}