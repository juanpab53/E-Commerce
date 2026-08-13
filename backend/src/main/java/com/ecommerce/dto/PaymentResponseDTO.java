package com.ecommerce.dto;


public record PaymentResponseDTO(
    Long id,
    Long orderId,
    Double amount,
    String paymentDate,
    String paymentMethod, 
    String orderStatus
) {}
