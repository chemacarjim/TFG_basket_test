package com.chema.backend.service.impl;

import com.chema.backend.domain.entity.*;
import com.chema.backend.dto.admin.*;
import com.chema.backend.exception.NotFoundException;
import com.chema.backend.repository.TestSessionRepository;
import com.chema.backend.repository.*;
import com.chema.backend.service.AdminContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminContentServiceImpl implements AdminContentService {

    private final TestRepository testRepo;
    private final QuestionRepository questionRepo;
    private final AdminUserRepository adminRepo;
    private final TestSessionRepository sessionRepo;

    @Override
    public List<TestAdminDto> listTests() {
        return testRepo.findAll().stream()
                .sorted(Comparator.comparing(TestEntity::getCreatedAt).reversed())
                .map(t -> new TestAdminDto(t.getId(), t.getTitle(), t.getDescription(), t.getIsActive()))
                .toList();
    }

    @Override
    @Transactional
    public TestAdminDto createTest(TestCreateDto dto, String adminEmail) {
        AdminUser admin = adminRepo.findByEmail(adminEmail)
                .orElseThrow(() -> new NotFoundException("ADMIN_NOT_FOUND", "Admin " + adminEmail + " no existe"));
        TestEntity t = TestEntity.builder()
                .title(dto.title())
                .description(dto.description())
                .isActive(dto.isActive() != null ? dto.isActive() : Boolean.FALSE)
                .createdBy(admin)
                .build();
        t = testRepo.save(t);
        return new TestAdminDto(t.getId(), t.getTitle(), t.getDescription(), t.getIsActive());
    }

    @Override
    @Transactional
    public TestAdminDto updateTest(Long id, TestUpdateDto dto) {
        TestEntity t = testRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("TEST_NOT_FOUND", "No existe test id=" + id));
        if (dto.title() != null) t.setTitle(dto.title());
        if (dto.description() != null) t.setDescription(dto.description());
        if (dto.isActive() != null) t.setIsActive(dto.isActive());
        t = testRepo.save(t);
        return new TestAdminDto(t.getId(), t.getTitle(), t.getDescription(), t.getIsActive());
    }

    @Override
    @Transactional
    public void deleteTest(Long id) {
        if (!testRepo.existsById(id))
            throw new NotFoundException("TEST_NOT_FOUND", "No existe test id=" + id);
        testRepo.deleteById(id);
    }

    @Override
    public List<QuestionAdminDto> listQuestions(Long testId) {
        TestEntity t = testRepo.findById(testId)
                .orElseThrow(() -> new NotFoundException("TEST_NOT_FOUND", "No existe test id=" + testId));
        return questionRepo.findByTestOrderByOrderIndexAsc(t).stream()
                .map(q -> new QuestionAdminDto(q.getId(), t.getId(), q.getPrompt(), q.getPossessionTime(), q.getImageUrl(), q.getOrderIndex()))
                .toList();
    }

    @Override
    @Transactional
    public QuestionAdminDto createQuestion(Long testId, QuestionUpsertDto dto) {
        TestEntity t = testRepo.findById(testId)
                .orElseThrow(() -> new NotFoundException("TEST_NOT_FOUND", "No existe test id=" + testId));
        Question q = Question.builder()
                .test(t)
                .prompt(dto.prompt())
                .possessionTime(dto.possessionTime())
                .imageUrl(dto.imageUrl())
                .orderIndex(dto.orderIndex() != null ? dto.orderIndex() : 0)
                .build();
        q = questionRepo.save(q);
        return new QuestionAdminDto(q.getId(), t.getId(), q.getPrompt(), q.getPossessionTime(), q.getImageUrl(), q.getOrderIndex());
    }

    @Override
    @Transactional
    public QuestionAdminDto updateQuestion(Long questionId, QuestionUpsertDto dto) {
        Question q = questionRepo.findById(questionId)
                .orElseThrow(() -> new NotFoundException("QUESTION_NOT_FOUND", "No existe pregunta id=" + questionId));
        if (dto.prompt() != null) q.setPrompt(dto.prompt());
        if (dto.possessionTime() != null) q.setPossessionTime(dto.possessionTime());
        if (dto.imageUrl() != null) q.setImageUrl(dto.imageUrl());
        if (dto.orderIndex() != null) q.setOrderIndex(dto.orderIndex());
        q = questionRepo.save(q);
        return new QuestionAdminDto(q.getId(), q.getTest().getId(), q.getPrompt(), q.getPossessionTime(), q.getImageUrl(), q.getOrderIndex());
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        if (!questionRepo.existsById(questionId))
            throw new NotFoundException("QUESTION_NOT_FOUND", "No existe pregunta id=" + questionId);
        questionRepo.deleteById(questionId);
    }

    @Override
    public AdminSessionListDto listFinishedSessions(Long testId, int limit, int offset) {
        var list = sessionRepo.findFinishedByTestIdPaged(testId, limit, offset);
        var items = list.stream().map(s ->
            new AdminSessionItemDto(
                s.getId(),
                s.getParticipantName(),
                s.getParticipantSurname(),
                s.getFinishedAt() != null ? s.getFinishedAt().toString() : null,
                s.getScore(),
                s.getTotal()
            )
        ).toList();

        boolean hasMore = items.size() == limit;
        return new AdminSessionListDto(items, offset + items.size(), hasMore);
    }
}
