package com.ecommerce.dto;

public record UsuarioResponseDTO(Long id,
    String nombre,
    String email,
    String direccion,
    String rol
) {}
