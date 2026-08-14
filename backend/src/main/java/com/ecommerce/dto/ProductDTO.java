package com.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductDTO(
    @NotBlank(message = "Product name is required")
    String name,

    String description,

    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be greater than zero")
    Double price,

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    Integer quantity,

    @NotNull(message = "Category ID is required")
    Long categoryId
) { }
