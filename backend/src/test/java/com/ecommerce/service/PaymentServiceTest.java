package com.ecommerce.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.ecommerce.dto.PaymentDTO;
import com.ecommerce.dto.PaymentResponseDTO;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.PaymentMethod;
import com.ecommerce.model.Payment;
import com.ecommerce.model.Order;
import com.ecommerce.repository.PaymentRepository;
import com.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Order exampleOrder;
    private Payment examplePayment;
    private PaymentDTO paymentDTO;

    @BeforeEach
    void setUp() {
        exampleOrder = new Order();
        exampleOrder.setId(1L);
        exampleOrder.setTotal(1500.0);
        exampleOrder.setStatus(OrderStatus.PENDING);

        examplePayment = new Payment();
        examplePayment.setId(10L);
        examplePayment.setOrder(exampleOrder);
        examplePayment.setAmount(1500.0);
        examplePayment.setPaymentDate(LocalDateTime.now());
        examplePayment.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        paymentDTO = new PaymentDTO(1L, PaymentMethod.CREDIT_CARD);
    }

    // --- PAYMENT PROCESSING TESTS ---

    @Test
    @DisplayName("Should process the payment successfully and update the order status")
    void processPaymentSuccess() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(exampleOrder));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenReturn(examplePayment);
        
        PaymentResponseDTO result = paymentService.processPayment(paymentDTO);

        assertNotNull(result);
        // Verify that the in-memory order status changed
        assertEquals(OrderStatus.PAID, exampleOrder.getStatus());
        // Verify that both changes are saved
        verify(orderRepository).save(exampleOrder);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should fail if the order does not exist")
    void processPaymentFailsOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> paymentService.processPayment(paymentDTO));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should fail if the order is already paid")
    void processPaymentFailsAlreadyPaid() {
        exampleOrder.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(exampleOrder));

        assertThrows(BusinessRuleException.class, () -> paymentService.processPayment(paymentDTO));
        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should fail if the order is cancelled")
    void processPaymentFailsCancelled() {
        exampleOrder.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(exampleOrder));

        assertThrows(BusinessRuleException.class, () -> paymentService.processPayment(paymentDTO));
    }

    @Test
    @DisplayName("Should fail if a payment record already exists for the order")
    void processPaymentFailsDuplicate() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(exampleOrder));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(examplePayment));

        assertThrows(BusinessRuleException.class, () -> paymentService.processPayment(paymentDTO));
    }

    // --- SEARCH TESTS ---

    @Test
    @DisplayName("Should find a payment by ID")
    void findByIdSuccess() {
        when(paymentRepository.findById(10L)).thenReturn(Optional.of(examplePayment));

        PaymentResponseDTO result = paymentService.findById(10L);

        assertNotNull(result);
        assertEquals(10L, result.id());
    }

    @Test
    @DisplayName("Should find a payment by order ID")
    void findByOrderIdSuccess() {
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(examplePayment));

        PaymentResponseDTO result = paymentService.findByOrderId(1L);

        assertNotNull(result);
        assertEquals(1L, result.orderId());
    }

    @Test
    @DisplayName("Should list all payments")
    void listAllSuccess() {
        when(paymentRepository.findAll()).thenReturn(List.of(examplePayment));

        List<PaymentResponseDTO> result = paymentService.listAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
