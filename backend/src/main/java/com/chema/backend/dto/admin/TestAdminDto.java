package com.chema.backend.dto.admin;

public record TestAdminDto(
        Long id,
        String title,
        String description,
        Boolean isActive
) { }
