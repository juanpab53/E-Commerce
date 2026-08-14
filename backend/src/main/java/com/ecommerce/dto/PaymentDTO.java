package com.ecommerce.dto;

import jakarta.validation.constraints.NotNull;

import com.ecommerce.model.PaymentMethod;

public record PaymentDTO(
    @NotNull(message = "Order ID is required")
    Long orderId,

    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod
) { }
