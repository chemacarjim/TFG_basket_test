package com.chema.backend.dto;

public record QuestionDto(
        Long id,
        String prompt,
        Integer possessionTime,
        String imageUrl
) { }
