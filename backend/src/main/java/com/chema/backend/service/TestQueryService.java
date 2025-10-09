package com.chema.backend.service;

import com.chema.backend.dto.TestDetailDto;
import com.chema.backend.dto.TestSummaryDto;

import java.util.List;

public interface TestQueryService {
    List<TestSummaryDto> listActiveTests();
    TestDetailDto getTestDetail(Long testId);
}
