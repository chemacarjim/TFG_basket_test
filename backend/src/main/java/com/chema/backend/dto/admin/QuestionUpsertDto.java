package com.chema.backend.dto.admin;

public record QuestionUpsertDto(
        String prompt,
        Integer possessionTime,
        String imageUrl,
        Integer orderIndex
) { }
