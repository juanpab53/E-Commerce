package com.ecommerce.dto;

import java.util.List;

public record OrderResponseDTO(
    Long id,
    String orderDate,
    String status,
    Double total,
    List<OrderItemResponseDTO> orderItems

) {}
