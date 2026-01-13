package com.Ecommerce.PruebaE_Commerce.dto;


public record PagoResponseDTO(
    Long id,
    Long pedidoId,
    Double monto,
    String fechaPago,
    String metodoPago, 
    String estadoPedidoActual
) {}
