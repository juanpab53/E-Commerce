package com.ecommerce.dto;

import com.ecommerce.model.MetodoPago;
import jakarta.validation.constraints.NotNull;

public record PagoDTO(
    @NotNull(message = "El ID del pedido es obligatorio")
    Long pedidoId,

    @NotNull(message = "El método de pago es obligatorio")
    MetodoPago metodoPago 
) {}
