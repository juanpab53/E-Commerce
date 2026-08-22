package com.ecommerce.identity.infrastructure.security;

public record LoginResponseDTO(
                String username,
                String token,
                String message) {
}
