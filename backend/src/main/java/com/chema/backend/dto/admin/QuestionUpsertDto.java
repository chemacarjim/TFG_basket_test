package com.chema.backend.dto.admin;

import com.chema.backend.domain.entity.ChoiceValue;

public record QuestionUpsertDto(
        String prompt,
        Integer possessionTime,
        String imageUrl,
        ChoiceValue correctValue
) { }
