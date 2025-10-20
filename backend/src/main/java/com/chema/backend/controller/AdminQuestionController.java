package com.chema.backend.controller;

import com.chema.backend.dto.admin.*;
import com.chema.backend.service.AdminContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/tests/{testId}/questions")
@RequiredArgsConstructor
public class AdminQuestionController {

    private final AdminContentService service;

    @GetMapping
    public ResponseEntity<List<QuestionAdminDto>> list(@PathVariable Long testId) {
        return ResponseEntity.ok(service.listQuestions(testId));
    }

    @PostMapping
    public ResponseEntity<QuestionAdminDto> create(@PathVariable Long testId,
                                                   @RequestBody QuestionUpsertDto dto) {
        var created = service.createQuestion(testId, dto);
        return ResponseEntity.created(URI.create("/api/v1/admin/tests/" + testId + "/questions/" + created.id()))
                .body(created);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionAdminDto> update(@PathVariable Long testId,
                                                   @PathVariable Long questionId,
                                                   @RequestBody QuestionUpsertDto dto) {
        // testId no es necesario en servicio para update, pero lo mantenemos en ruta por coherencia.
        return ResponseEntity.ok(service.updateQuestion(questionId, dto));
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> delete(@PathVariable Long testId,
                                       @PathVariable Long questionId) {
        service.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
