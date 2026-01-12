package com.Ecommerce.PruebaE_Commerce.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PedidoDTO(
    @NotNull(message = "El Id del usuario es obligatorio")
    Long usuarioID,

    @NotNull(message = "La lista de productos no puede ser nula")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    List<DetallePedidoDTO> detalles
) {}
