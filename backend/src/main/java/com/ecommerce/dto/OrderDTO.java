package com.ecommerce.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record OrderDTO(
    @NotNull(message = "User ID is required")
    Long userId,

    @NotNull(message = "The product list cannot be null")
    List<OrderItemDTO> orderItems
) { }
