package com.chema.backend.dto.admin;

import java.time.OffsetDateTime;

public record AdminUserDto(
    Long id,
    String email,
    OffsetDateTime createdAt,
    OffsetDateTime lastLoginAt,
    Boolean isSuperAdmin
) { }
