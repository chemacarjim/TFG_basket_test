package com.chema.backend.dto.admin;

public record AdminSessionItemDto(
        Long sessionId,
        String name,
        String surname,
        String finishedAt,
        Integer score,
        Integer total
) { }
