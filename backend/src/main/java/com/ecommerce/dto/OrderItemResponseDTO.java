package com.ecommerce.dto;

public record OrderItemResponseDTO(
    Long id,
    Long productId,
    String productName,
    Integer quantity,
    Double unitPrice,
    Double subtotal

) { }
