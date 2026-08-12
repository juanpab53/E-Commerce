package com.ecommerce.dto;

public record DetallePedidoResponseDTO(
    Long id,
    Long productoId,
    String nombreProducto,
    Integer cantidad,
    Double precioUnitario,
    Double subtotal

) {}