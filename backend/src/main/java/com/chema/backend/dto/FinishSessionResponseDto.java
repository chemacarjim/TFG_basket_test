package com.chema.backend.dto;

import java.util.List;

public record FinishSessionResponseDto(
        Integer score,
        Integer total,
        Long durationMs,
        String finishedAt,
        List<FinishSessionItemDto> items
) { }
