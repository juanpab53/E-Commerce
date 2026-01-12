package com.Ecommerce.PruebaE_Commerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DetallePedidoDTO(

    @NotNull(message = "El ID de producto es obligatorio") 
    Long productoId,

    @NotNull(message = "La cantidad es obligatoria") 
    @Min(value = 1, message = "La cantidad mínima es 1") 
    Integer cantidad
) {}
