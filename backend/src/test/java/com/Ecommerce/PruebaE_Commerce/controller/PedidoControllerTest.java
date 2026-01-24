package com.Ecommerce.PruebaE_Commerce.controller;

import com.Ecommerce.PruebaE_Commerce.dto.DetallePedidoDTO;
import com.Ecommerce.PruebaE_Commerce.dto.DetallePedidoResponseDTO;
import com.Ecommerce.PruebaE_Commerce.dto.PedidoDTO;
import com.Ecommerce.PruebaE_Commerce.dto.PedidoResponseDTO;
import com.Ecommerce.PruebaE_Commerce.model.Estado;
import com.Ecommerce.PruebaE_Commerce.service.PedidoService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_pedido",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "supabase.url=http://localhost:54321",
        "supabase.key=testkeyfake"
})
public class PedidoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private PedidoService pedidoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("POST /pedidos - Crear pedido con DTO")
    void crearPedido() throws Exception {
        DetallePedidoDTO detalle = new DetallePedidoDTO(1L, 2);
        List<DetallePedidoDTO> detalles = List.of(detalle);

        PedidoDTO pedidoDto = new PedidoDTO(1L, detalles);

        DetallePedidoResponseDTO detRes = new DetallePedidoResponseDTO(1L, 1L, "Producto Prueba", 2, 50.0, 100.0);
        PedidoResponseDTO respuestaEsperada = new PedidoResponseDTO(1L, LocalDateTime.now().toString(), "PENDIENTE",
                100.0, List.of(detRes));

        when(pedidoService.crearPedido(any(PedidoDTO.class))).thenReturn(respuestaEsperada);

        mockMvc.perform(post("/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pedidoDto)))
                .andDo(result -> {
                    if (result.getResponse().getStatus() == 500) {
                        String errorString = "ERROR DETECTADO: " + result.getResolvedException().getMessage();
                        System.out.println(errorString);
                    }
                })
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /pedidos - Listar todos")
    void listarTodos() throws Exception {
        when(pedidoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/pedidos"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /pedidos/usuario/{usuarioId} - Listar por usuario")
    void listarPorUsuario() throws Exception {
        when(pedidoService.listarPedidosPorUsuario(1L)).thenReturn(List.of());

        mockMvc.perform(get("/pedidos/usuario/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /pedidos/{id}/estado - Actualizar estado")
    void actualizarEstado() throws Exception {
        DetallePedidoResponseDTO detRes = new DetallePedidoResponseDTO(1L, 1L, "Producto Prueba", 2, 50.0, 100.0);
        PedidoResponseDTO res = new PedidoResponseDTO(1L, LocalDateTime.now().toString(), "ENVIADO", 500.0, List.of(detRes));

        when(pedidoService.cambiarEstado(anyLong(), any(Estado.class))).thenReturn(res);

        mockMvc.perform(patch("/pedidos/1/estado")
                .param("nuevoEstado", "ENVIADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENVIADO"));
    }

    @Test
    @DisplayName("DELETE /pedidos/{id} - Cancelar/Eliminar pedido")
    void eliminarPedido() throws Exception {
        mockMvc.perform(delete("/pedidos/1/cancelar"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /pedidos/{id} - Buscar por ID")
    void buscarPorId() throws Exception {
        DetallePedidoResponseDTO detRes = new DetallePedidoResponseDTO(1L, 1L, "Producto Prueba", 2, 50.0, 100.0);
        PedidoResponseDTO res = new PedidoResponseDTO(1L, LocalDateTime.now().toString(), "PENDIENTE", 100.0, List.of(detRes));

        when(pedidoService.buscarPorId(anyLong())).thenReturn(res);

        mockMvc.perform(get("/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}