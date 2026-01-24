package com.Ecommerce.PruebaE_Commerce.controller;

import com.Ecommerce.PruebaE_Commerce.dto.UsuarioRegistroDTO;
import com.Ecommerce.PruebaE_Commerce.dto.UsuarioResponseDTO;
import com.Ecommerce.PruebaE_Commerce.service.UsuarioService;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "supabase.url=http://localhost:54321",
        "supabase.key=testkeyfake"
})
public class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private UsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("POST /usuarios/registro - Debería registrar un usuario")
    void registrarUsuario() throws Exception {
        UsuarioRegistroDTO registro = new UsuarioRegistroDTO("Juan", "Perez", "juan@test.com", "123456789", "Colombia",
                "Bogota", "Calle 1");
        UsuarioResponseDTO respuesta = new UsuarioResponseDTO(1L, "Juan", "juan@test.com", "Colombia, Bogota, Calle 1",
                "USER");

        when(usuarioService.registrarUsuario(any(UsuarioRegistroDTO.class))).thenReturn(respuesta);

        mockMvc.perform(post("/usuarios/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    @DisplayName("GET /usuarios - Debería listar todos los usuarios")
    void listarUsuarios() throws Exception {
        UsuarioResponseDTO u1 = new UsuarioResponseDTO(1L, "Juan", "juan@test.com", "Colombia, Bogota, Calle 1",
                "USER");
        when(usuarioService.listarUsuarios()).thenReturn(List.of(u1));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    @DisplayName("GET /usuarios/{id} - Debería buscar por ID")
    void buscarPorId() throws Exception {
        UsuarioResponseDTO respuesta = new UsuarioResponseDTO(1L, "Juan", "juan@test.com", "Colombia, Bogota, Calle 1",
                "USER");
        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(respuesta);

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @DisplayName("PUT /usuarios/{id} - Debería actualizar un usuario")
    void actualizarUsuario() throws Exception {
        UsuarioRegistroDTO actualizacion = new UsuarioRegistroDTO("JuanMod", "PerezMod", "juan@test.com", "123456789",
                "Colombia", "Bogota", "Calle 1");
        UsuarioResponseDTO respuesta = new UsuarioResponseDTO(1L, "JuanMod", "juan@test.com",
                "Colombia, Bogota, Calle 1",
                "USER");

        when(usuarioService.actualizar(anyLong(), any(UsuarioRegistroDTO.class))).thenReturn(respuesta);

        mockMvc.perform(put("/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizacion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("JuanMod"));
    }

    @Test
    @DisplayName("DELETE /usuarios/{id} - Debería eliminar un usuario")
    void eliminarUsuario() throws Exception {
        doNothing().when(usuarioService).eliminarUsuario(1L);

        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isNoContent());
    }
}