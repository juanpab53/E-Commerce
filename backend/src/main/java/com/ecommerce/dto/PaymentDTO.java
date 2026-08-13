package com.ecommerce.dto;

import com.ecommerce.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record PaymentDTO(
    @NotNull(message = "Order ID is required")
    Long orderId,

    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod 
) {}
