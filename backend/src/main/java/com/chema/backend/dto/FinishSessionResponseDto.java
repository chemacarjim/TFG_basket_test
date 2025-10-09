package com.chema.backend.dto;

public record FinishSessionResponseDto(
        Integer score,
        Integer total,
        Long durationMs
) { }
