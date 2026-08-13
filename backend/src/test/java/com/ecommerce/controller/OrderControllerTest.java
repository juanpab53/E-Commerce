package com.ecommerce.controller;

import com.ecommerce.dto.OrderItemDTO;
import com.ecommerce.dto.OrderItemResponseDTO;
import com.ecommerce.dto.OrderDTO;
import com.ecommerce.dto.OrderResponseDTO;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.service.OrderService;
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
public class OrderControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private OrderService orderService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("POST /orders - Create order with DTO")
    void createOrder() throws Exception {
        OrderItemDTO orderItem = new OrderItemDTO(1L, 2);
        List<OrderItemDTO> orderItems = List.of(orderItem);

        OrderDTO orderDto = new OrderDTO(1L, orderItems);

        OrderItemResponseDTO itemRes = new OrderItemResponseDTO(1L, 1L, "Producto Prueba", 2, 50.0, 100.0);
        OrderResponseDTO expectedResponse = new OrderResponseDTO(1L, LocalDateTime.now().toString(), "PENDING",
                100.0, List.of(itemRes));

        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /orders - List all")
    void listAll() throws Exception {
        when(orderService.listAll()).thenReturn(List.of());

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /orders/user/{userId} - List by user")
    void listByUser() throws Exception {
        when(orderService.listOrdersByUser(1L)).thenReturn(List.of());

        mockMvc.perform(get("/orders/user/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /orders/{id}/status - Update status")
    void updateStatus() throws Exception {
        OrderItemResponseDTO itemRes = new OrderItemResponseDTO(1L, 1L, "Producto Prueba", 2, 50.0, 100.0);
        OrderResponseDTO res = new OrderResponseDTO(1L, LocalDateTime.now().toString(), "SHIPPED", 500.0, List.of(itemRes));

        when(orderService.changeStatus(anyLong(), any(OrderStatus.class))).thenReturn(res);

        mockMvc.perform(patch("/orders/1/status")
                .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test
    @DisplayName("DELETE /orders/{id} - Cancel/Delete order")
    void cancelOrder() throws Exception {
        mockMvc.perform(delete("/orders/1/cancel"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /orders/{id} - Find by ID")
    void findById() throws Exception {
        OrderItemResponseDTO itemRes = new OrderItemResponseDTO(1L, 1L, "Producto Prueba", 2, 50.0, 100.0);
        OrderResponseDTO res = new OrderResponseDTO(1L, LocalDateTime.now().toString(), "PENDING", 100.0, List.of(itemRes));

        when(orderService.findById(anyLong())).thenReturn(res);

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
