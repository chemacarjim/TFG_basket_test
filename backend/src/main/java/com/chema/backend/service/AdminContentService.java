package com.chema.backend.service;

import com.chema.backend.dto.admin.*;

import java.util.List;

public interface AdminContentService {
    // Tests
    List<TestAdminDto> listTests();
    TestAdminDto createTest(TestCreateDto dto, String adminEmail);
    TestAdminDto updateTest(Long id, TestUpdateDto dto);
    void deleteTest(Long id);

    // Questions
    List<QuestionAdminDto> listQuestions(Long testId);
    QuestionAdminDto createQuestion(Long testId, QuestionUpsertDto dto);
    QuestionAdminDto updateQuestion(Long questionId, QuestionUpsertDto dto);
    void deleteQuestion(Long questionId);

    // Sessions
    AdminSessionListDto listFinishedSessions(Long testId, int limit, int offset);
}
