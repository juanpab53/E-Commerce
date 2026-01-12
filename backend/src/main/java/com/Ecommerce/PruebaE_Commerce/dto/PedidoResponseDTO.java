package com.Ecommerce.PruebaE_Commerce.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
    Long id,
    LocalDateTime fechaPedido,
    String estado,
    Double total,
    List<DetallePedidoResponseDTO> detalles

) {}
