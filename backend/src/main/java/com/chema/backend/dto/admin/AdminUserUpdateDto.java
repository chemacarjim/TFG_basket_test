package com.chema.backend.dto.admin;

public record AdminUserUpdateDto(
    String email,
    String password,
    Boolean isSuperAdmin
) { }
