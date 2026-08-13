package com.ecommerce.controller;

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

import com.ecommerce.dto.PaymentDTO;
import com.ecommerce.dto.PaymentResponseDTO;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.PaymentMethod;
import com.ecommerce.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb_pago",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "supabase.url=http://localhost:54321",
        "supabase.key=testkeyfake"
})
public class PaymentControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private PaymentService paymentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("POST /payments - Process payment successfully")
    void processPayment() throws Exception {
        PaymentDTO paymentDto = new PaymentDTO(1L, PaymentMethod.CASH);

        PaymentResponseDTO mockResponse = new PaymentResponseDTO(1L, 1L, 150.0, LocalDate.now().toString(), PaymentMethod.CASH.name(), OrderStatus.PAID.name());

        when(paymentService.processPayment(any(PaymentDTO.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderStatus").value("PAID"));
    }

    @Test
    @DisplayName("GET /payments/order/{orderId} - Find payment by order ID")
    void findByOrderId() throws Exception {
        PaymentResponseDTO mockResponse = new PaymentResponseDTO(1L, 1L, 150.0, LocalDate.now().toString(), PaymentMethod.CASH.name(), OrderStatus.PAID.name());

        when(paymentService.findByOrderId(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/payments/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1L));
    }

    @Test
    @DisplayName("GET /payments - List all payments")
    void listAll() throws Exception {
        PaymentResponseDTO mockResponse = new PaymentResponseDTO(1L, 1L, 150.0, LocalDate.now().toString(), PaymentMethod.CASH.name(), OrderStatus.PAID.name());

        when(paymentService.listAll()).thenReturn(List.of(mockResponse));

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }
}
