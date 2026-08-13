package com.ecommerce.dto;

public record ProductResponseDTO(
    Long id,
    String name,
    String description,
    Double price,
    Integer quantity,
    String categoryName
) {}
