package com.Ecommerce.PruebaE_Commerce.dto;

import com.Ecommerce.PruebaE_Commerce.model.MetodoPago;
import jakarta.validation.constraints.NotNull;

public record PagoDTO(
    @NotNull(message = "El ID del pedido es obligatorio")
    Long pedidoId,

    @NotNull(message = "El método de pago es obligatorio")
    MetodoPago metodoPago 
) {}
