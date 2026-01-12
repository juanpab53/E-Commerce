package com.Ecommerce.PruebaE_Commerce.dto;

public record DetallePedidoResponseDTO(

    Long productoId,
    String nombreProducto,
    Integer cantidad,
    Double precioUnitario,
    Double subtotal

) {}