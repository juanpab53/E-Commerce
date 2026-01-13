package com.Ecommerce.PruebaE_Commerce.dto;

public record ProductoResponseDTO(
    Long id,
    String nombre,
    String descripcion,
    Double precio,
    Integer cantidad,
    String nombreCategoria
) {}