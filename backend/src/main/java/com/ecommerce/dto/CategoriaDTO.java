package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaDTO(
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    String nombre
) {}
