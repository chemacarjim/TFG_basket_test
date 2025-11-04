package com.chema.backend.dto;

import com.chema.backend.domain.entity.ChoiceValue;

public record QuestionDto(
        Long id,
        String prompt,
        Integer possessionTime,
        String imageUrl,
        ChoiceValue correctValue
) { }
