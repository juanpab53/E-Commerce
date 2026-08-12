package com.ecommerce.dto;

public record ProductoResponseDTO(
    Long id,
    String nombre,
    String descripcion,
    Double precio,
    Integer cantidad,
    String nombreCategoria
) {}