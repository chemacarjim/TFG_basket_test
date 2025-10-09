package com.chema.backend.dto;

import java.util.List;

public record TestDetailDto(
        Long id,
        String title,
        List<QuestionDto> questions
) { }
