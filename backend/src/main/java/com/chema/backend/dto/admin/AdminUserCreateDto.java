package com.chema.backend.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record AdminUserCreateDto(
    @NotBlank String email,
    @NotBlank String password,
    @NotNull Boolean isSuperAdmin
) { }
