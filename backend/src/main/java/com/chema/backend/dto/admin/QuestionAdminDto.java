package com.chema.backend.dto.admin;

public record QuestionAdminDto(
        Long id,
        Long testId,
        String prompt,
        Integer possessionTime,
        String imageUrl
) { }
