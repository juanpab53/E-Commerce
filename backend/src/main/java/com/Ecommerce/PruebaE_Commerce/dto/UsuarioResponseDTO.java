package com.Ecommerce.PruebaE_Commerce.dto;

public record UsuarioResponseDTO(Long id,
    String nombre,
    String email,
    String direccion,
    String rol
) {}
