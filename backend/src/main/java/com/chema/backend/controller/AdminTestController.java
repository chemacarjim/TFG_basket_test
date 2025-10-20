package com.chema.backend.controller;

import com.chema.backend.dto.admin.*;
import com.chema.backend.service.AdminContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/tests")
@RequiredArgsConstructor
public class AdminTestController {

    private final AdminContentService service;

    @GetMapping
    public ResponseEntity<List<TestAdminDto>> list() {
        return ResponseEntity.ok(service.listTests());
    }

    @PostMapping
    public ResponseEntity<TestAdminDto> create(@RequestBody @Valid TestCreateDto dto,
                                               @AuthenticationPrincipal User admin) {
        var created = service.createTest(dto, admin.getUsername());
        return ResponseEntity.created(URI.create("/api/v1/admin/tests/" + created.id()))
                .body(created);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TestAdminDto> update(@PathVariable Long id,
                                               @RequestBody @Valid TestUpdateDto dto) {
        return ResponseEntity.ok(service.updateTest(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteTest(id);
        return ResponseEntity.noContent().build();
    }
}
