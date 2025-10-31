package com.chema.backend.service.impl;

import com.chema.backend.domain.entity.TestEntity;
import com.chema.backend.dto.TestDetailDto;
import com.chema.backend.dto.TestSummaryDto;
import com.chema.backend.exception.NotFoundException;
import com.chema.backend.mapper.TestMapper;
import com.chema.backend.repository.QuestionRepository;
import com.chema.backend.repository.TestRepository;
import com.chema.backend.service.TestQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestQueryServiceImpl implements TestQueryService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;

    @Override
    public List<TestSummaryDto> listActiveTests() {
        return testRepository.findByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(TestMapper::toSummary)
                .toList();
    }

    @Override
    public TestDetailDto getTestDetail(Long testId) {
        TestEntity test = testRepository.findById(testId)
                .orElseThrow(() -> new NotFoundException("TEST_NOT_FOUND", "No existe el test con id=" + testId));

        var questions = questionRepository.findByTestOrderByIsActiveAsc(test)
                .stream()
                .toList();

        return TestMapper.toDetail(test, questions);
    }
}
