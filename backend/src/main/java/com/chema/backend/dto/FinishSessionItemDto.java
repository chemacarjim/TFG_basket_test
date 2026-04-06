package com.chema.backend.dto;

import com.chema.backend.domain.entity.ChoiceValue;

public record FinishSessionItemDto(
        Long questionId,
        String questionPrompt,
        ChoiceValue selectedValue,
        ChoiceValue correctValue,
        Boolean isCorrect,
        Long responseTimeMs
) { }

