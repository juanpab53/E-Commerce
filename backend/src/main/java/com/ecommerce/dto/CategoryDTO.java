package com.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryDTO(
    @NotBlank(message = "Category name is required")
    String name
) { }
