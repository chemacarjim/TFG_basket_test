package com.chema.backend.dto;

import java.util.List;

public record SubmitResponsesRequestDto(
        List<SubmitResponsesItemDto> items
) { }
