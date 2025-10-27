package com.chema.backend.controller;

import com.chema.backend.dto.*;
import com.chema.backend.service.SessionService;
import com.chema.backend.service.TestQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class PublicTestController {

    private final TestQueryService testQueryService;
    private final SessionService sessionService;

    // 1) Listar tests activos
    @GetMapping("/tests/active")
    public ResponseEntity<List<TestSummaryDto>> listActive() {
        return ResponseEntity.ok(testQueryService.listActiveTests());
    }

    // 2) Obtener detalle de un test (sin soluciones)
    @GetMapping("/tests/{id}")
    public ResponseEntity<TestDetailDto> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(testQueryService.getTestDetail(id));
    }

    // 3) Crear sesión para un test
    @PostMapping("/tests/{id}/sessions")
    public ResponseEntity<CreateSessionResponseDto> createSession(
            @PathVariable Long id,
            @RequestBody CreateSessionRequestDto req
    ) {
        var res = sessionService.createSession(id, req);
        return ResponseEntity.ok(res);
    }

    // 4) Enviar lote de respuestas
    @PostMapping("/sessions/{sid}/responses")
    public ResponseEntity<Void> submitResponses(
            @PathVariable("sid") Long sessionId,
            @RequestBody SubmitResponsesRequestDto request
    ) {
        sessionService.submitResponses(sessionId, request);
        return ResponseEntity.noContent().build();
    }

    // 5) Finalizar sesión y devolver resumen
    @PostMapping("/sessions/{sid}/finish")
    public ResponseEntity<FinishSessionResponseDto> finish(
            @PathVariable("sid") Long sessionId
    ) {
        return ResponseEntity.ok(sessionService.finishSession(sessionId));
    }
}
