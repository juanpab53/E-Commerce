package com.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductoDTO(
    @NotBlank(message = "El nombre del producto es obligatorio")
    String nombre,

    String descripcion,

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser mayor a cero")
    Double precio,

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "El stock no puede ser negativo")
    Integer cantidad,

    @NotNull(message = "Debe especificar el ID de la categoría")
    Long categoriaId
) {}