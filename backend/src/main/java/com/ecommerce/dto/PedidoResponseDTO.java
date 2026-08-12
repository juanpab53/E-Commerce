package com.ecommerce.dto;

import java.util.List;

public record PedidoResponseDTO(
    Long id,
    String fechaPedido,
    String estado,
    Double total,
    List<DetallePedidoResponseDTO> detalles

) {}
