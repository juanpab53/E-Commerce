package com.ecommerce.dto;

public record UserResponseDTO(Long id,
    String name,
    String email,
    String address,
    String role
) {}
