package com.chema.backend.dto;

import com.chema.backend.domain.entity.ChoiceValue;

public record SubmitResponsesItemDto(
        Long questionId,
        ChoiceValue selectedValue,
        Long responseTimeMs
) { }
