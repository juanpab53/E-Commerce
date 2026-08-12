package com.ecommerce.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record PedidoDTO(
    @NotNull(message = "El Id del usuario es obligatorio")
    Long usuarioID,

    @NotNull(message = "La lista de productos no puede ser nula")
    List<DetallePedidoDTO> detalles
) {}
