package com.chema.backend.controller;

import com.chema.backend.dto.admin.AdminSessionListDto;
import com.chema.backend.service.AdminContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/tests/{testId}/sessions")
@RequiredArgsConstructor
public class AdminSessionController {

    private final AdminContentService service;

    @GetMapping
    public ResponseEntity<AdminSessionListDto> list(
            @PathVariable Long testId,
            @RequestParam(defaultValue = "15") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        return ResponseEntity.ok(service.listFinishedSessions(testId, limit, offset));
    }
}
