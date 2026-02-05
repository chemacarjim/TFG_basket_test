package com.chema.backend.controller;

import com.chema.backend.dto.admin.AdminSessionListDto;
import com.chema.backend.service.AdminContentService;
import com.chema.backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import com.chema.backend.repository.TestSessionRepository;
import com.chema.backend.domain.entity.TestSession;
import com.chema.backend.exception.BadRequestException;
import com.chema.backend.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api/v1/admin/tests/{testId}/sessions")
@RequiredArgsConstructor
public class AdminSessionController {

    private final AdminContentService service;
    private final ReportService reportService;
    private final TestSessionRepository sessionRepo;

    @GetMapping
    public ResponseEntity<AdminSessionListDto> list(
            @PathVariable Long testId,
            @RequestParam(defaultValue = "15") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        return ResponseEntity.ok(service.listFinishedSessions(testId, limit, offset));
    }

    @GetMapping(value = "/{sessionId}/report.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getReport(
        @PathVariable Long testId,
        @PathVariable Long sessionId
    ) {
        TestSession session = sessionRepo.findById(sessionId)
            .orElseThrow(() -> new NotFoundException("SESSION_NOT_FOUND", "Sesión no encontrada"));
        
        if(!session.getTest().getId().equals(testId)) {
            throw new BadRequestException("SESSION_MISMATCH", "Sesión no pertenece al test");
        }

        byte[] pdfbytes = reportService.buildPdf(sessionId);
        String filename = reportService.buildFileName(sessionId);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfbytes);
    }
}
