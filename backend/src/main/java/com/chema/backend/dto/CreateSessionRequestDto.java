package com.chema.backend.dto;

public record CreateSessionRequestDto(
        Long testId,            // si lo usas dentro del body; si viene en path, lo puedes ignorar
        String name,            // obligatorio
        String surname,         // obligatorio
        String birthDate,
        String country,
        String gender,
        String dominantHand,
        String position,
        String inasidnr,
        String event,
        String instructor
) { }
