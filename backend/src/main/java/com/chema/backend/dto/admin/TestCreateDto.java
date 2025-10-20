package com.chema.backend.dto.admin;

import jakarta.validation.constraints.NotBlank;

public record TestCreateDto(
        @NotBlank(message = "El título es obligatorio")
        String title,
        String description,
        Boolean isActive
) { }
