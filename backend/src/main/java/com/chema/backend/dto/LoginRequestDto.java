package com.chema.backend.dto;

public record LoginRequestDto(
        String email,
        String password
) { }
