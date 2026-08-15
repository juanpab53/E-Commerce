package com.ecommerce.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.ecommerce.dto.PaymentDTO;
import com.ecommerce.dto.PaymentResponseDTO;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.Payment;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.PaymentRepository;
import com.ecommerce.shared.domain.BusinessRuleException;
import com.ecommerce.shared.domain.NotFoundException;

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponseDTO processPayment(PaymentDTO paymentDto) {
        Order order = orderRepository.findById(paymentDto.orderId())
                .orElseThrow(() -> new NotFoundException(
                        "Cannot process the payment: Order not found with ID: " + paymentDto.orderId()));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new BusinessRuleException(
                    "Invalid operation: The order with ID " + paymentDto.orderId() + " has already been paid.");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessRuleException(
                    "Invalid operation: The order " + paymentDto.orderId() +
                            " cannot be paid because it is CANCELLED.");
        }

        paymentRepository.findByOrderId(paymentDto.orderId()).ifPresent(p -> {
            throw new BusinessRuleException(
                    "Conflict: A payment record already exists for the order ID: " + paymentDto.orderId());
        });

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotal());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(paymentDto.paymentMethod());

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        return toResponse(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentResponseDTO findById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment record not found with ID: " + id));
        return toResponse(payment);
    }

    @Transactional
    public PaymentResponseDTO findByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("No payment found for the order ID: " + orderId));
        return toResponse(payment);
    }

    @Transactional
    public List<PaymentResponseDTO> listAll() {
        return paymentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponseDTO toResponse(Payment payment) {
        return new PaymentResponseDTO(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getPaymentDate().toString(),
                payment.getPaymentMethod().name(),
                payment.getOrder().getStatus().name()
        );
    }
}
