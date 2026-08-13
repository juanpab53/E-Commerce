package com.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemDTO(

    @NotNull(message = "Product ID is required") 
    Long productId,

    @NotNull(message = "Quantity is required") 
    @Min(value = 1, message = "Minimum quantity is 1") 
    Integer quantity
) {}
