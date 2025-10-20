package com.chema.backend.dto.admin;

import jakarta.validation.constraints.*;

public record TestUpdateDto(
        @Size(min = 3, max = 255, message = "El título debe tener entre 3 y 255 caracteres")
        String title,
        String description,
        Boolean isActive
) { }
