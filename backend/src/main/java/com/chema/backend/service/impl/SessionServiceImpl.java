package com.chema.backend.service.impl;

import com.chema.backend.domain.entity.Question;
import com.chema.backend.domain.entity.ResponseEntity;
import com.chema.backend.domain.entity.TestEntity;
import com.chema.backend.domain.entity.TestSession;
import com.chema.backend.dto.CreateSessionRequestDto;
import com.chema.backend.dto.CreateSessionResponseDto;
import com.chema.backend.dto.FinishSessionResponseDto;
import com.chema.backend.dto.SubmitResponsesRequestDto;
import com.chema.backend.exception.BadRequestException;
import com.chema.backend.exception.NotFoundException;
import com.chema.backend.repository.QuestionRepository;
import com.chema.backend.repository.ResponseRepository;
import com.chema.backend.repository.TestRepository;
import com.chema.backend.repository.TestSessionRepository;
import com.chema.backend.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final TestRepository testRepository;
    private final TestSessionRepository sessionRepository;
    private final QuestionRepository questionRepository;
    private final ResponseRepository responseRepository;

    @Override
    @Transactional
    public CreateSessionResponseDto createSession(Long testId, CreateSessionRequestDto req) {
        TestEntity test = testRepository.findById(testId)
                .orElseThrow(() -> new NotFoundException("TEST_NOT_FOUND", "No existe el test con id=" + testId));

        if (req.name() == null || req.surname() == null || req.name().isBlank() || req.surname().isBlank()) {
            throw new BadRequestException("VALIDATION_ERROR", "Existen datos obligatorios que faltan.");
        }

        TestSession s = sessionRepository.save(TestSession.builder()
                .test(test)
                .participantName(req.name().trim())
                .participantSurname(req.surname().trim())
                .participantBirthDate(parseBirthDate(req.birthDate()))
                .participantCountry(emptyToNull(req.country()))
                .participantGender(emptyToNull(req.gender()))
                .participantDominantHand(emptyToNull(req.dominantHand()))
                .participantPosition(emptyToNull(req.position()))
                .participantInasidnr(emptyToNull(req.inasidnr()))
                .participantEvent(emptyToNull(req.event()))
                .participantInstructor(emptyToNull(req.instructor()))
                .build());

        s = sessionRepository.save(s);
        return new CreateSessionResponseDto(s.getId());
    }

    private static LocalDate parseBirthDate(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    private static String emptyToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    @Override
    @Transactional
    public void submitResponses(Long sessionId, SubmitResponsesRequestDto request) {
        TestSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND", "No existe la sesión con id=" + sessionId));

        if (request == null || request.items() == null || request.items().isEmpty()) {
            throw new BadRequestException("EMPTY_PAYLOAD", "El lote de respuestas está vacío.");
        }

        // Cache de preguntas (para validar pertenencia al test)
        Map<Long, Question> cache = new HashMap<>();

        for (var item : request.items()) {
            if (item.questionId() == null || item.selectedValue() == null) {
                throw new BadRequestException("INVALID_ITEM", "Cada ítem debe tener questionId y selectedValue.");
            }

            Question q = cache.computeIfAbsent(item.questionId(),
                    id -> questionRepository.findById(id)
                            .orElseThrow(() -> new NotFoundException("QUESTION_NOT_FOUND", "No existe la pregunta id=" + id)));

            // Validar que la pregunta pertenece al mismo test que la sesión
            if (!q.getTest().getId().equals(session.getTest().getId())) {
                throw new BadRequestException("QUESTION_MISMATCH",
                        "La pregunta " + q.getId() + " no pertenece al test de la sesión.");
            }

            ResponseEntity r = ResponseEntity.builder()
                    .session(session)
                    .question(q)
                    .selectedValue(item.selectedValue())
                    .responseTimeMs(item.responseTimeMs())
                    .isCorrect(null) // MVP: aún no calculamos corrección
                    .build();

            responseRepository.save(r);
        }
    }

    @Override
    @Transactional
    public FinishSessionResponseDto finishSession(Long sessionId) {
        TestSession s = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND", "No existe la sesión con id=" + sessionId));

        var now = OffsetDateTime.now();
        s.setFinishedAt(now);

        var responses = responseRepository.findBySession(s);
        int total = responses.size();
        int score = 0;   // MVP: cálculo real vendrá después

        s.setTotal(total);
        if (s.getStartedAt() != null) {
            long duration = java.time.Duration.between(s.getStartedAt(), now).toMillis();
            s.setDurationMs(duration);
        } else {
            s.setDurationMs(null);
        }
        s.setScore(score);

        sessionRepository.save(s);
        return new FinishSessionResponseDto(score, total, s.getDurationMs());
    }
}
