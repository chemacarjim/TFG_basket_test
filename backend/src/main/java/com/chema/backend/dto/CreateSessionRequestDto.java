package com.chema.backend.dto;

public record CreateSessionRequestDto(
        String anonUserCode  // opcional, puede venir null
) { }
