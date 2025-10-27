package com.chema.backend.dto.admin;

import java.util.List;

public record AdminSessionListDto(
        List<AdminSessionItemDto> items,
        Integer nextOffset,
        Boolean hasMore
) { }
