package com.Ecommerce.PruebaE_Commerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.Ecommerce.PruebaE_Commerce.dto.PagoDTO;
import com.Ecommerce.PruebaE_Commerce.dto.PagoResponseDTO;
import com.Ecommerce.PruebaE_Commerce.model.Estado;
import com.Ecommerce.PruebaE_Commerce.model.MetodoPago;
import com.Ecommerce.PruebaE_Commerce.service.PagoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_pago",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "supabase.url=http://localhost:54321",
        "supabase.key=testkeyfake"
})
public class PagoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private PagoService pagoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("POST /pagos - Procesar pago exitosamente")
    void procesarPago() throws Exception {
        PagoDTO pagoDto = new PagoDTO(1L, MetodoPago.EFECTIVO);

        PagoResponseDTO respuestaMock = new PagoResponseDTO(1L, 1L, 150.0, LocalDate.now().toString(), MetodoPago.EFECTIVO.name(), Estado.PAGADO.name());

        when(pagoService.procesarPago(any(PagoDTO.class))).thenReturn(respuestaMock);

        mockMvc.perform(post("/pagos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pagoDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estadoPedidoActual").value("PAGADO"));
    }

    @Test
    @DisplayName("GET /pagos/pedido/{pedidoId} - Buscar pago por ID de pedido")
    void buscarPorPedido() throws Exception {
        PagoResponseDTO respuestaMock = new PagoResponseDTO(1L, 1L, 150.0, LocalDate.now().toString(), MetodoPago.EFECTIVO.name(), Estado.PAGADO.name());

        when(pagoService.buscarPorPedido(1L)).thenReturn(respuestaMock);

        mockMvc.perform(get("/pagos/pedido/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pedidoId").value(1L));
    }

    @Test
    @DisplayName("GET /pagos - Listar todos los pagos")
    void listarTodos() throws Exception {
        PagoResponseDTO respuestaMock = new PagoResponseDTO(1L, 1L, 150.0, LocalDate.now().toString(), MetodoPago.EFECTIVO.name(), Estado.PAGADO.name());

        when(pagoService.listarTodos()).thenReturn(List.of(respuestaMock));

        mockMvc.perform(get("/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
