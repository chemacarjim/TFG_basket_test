package com.chema.backend.dto;

public record ApiErrorDto(
        String code,
        String message
) { }
