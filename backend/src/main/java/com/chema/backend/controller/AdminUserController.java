package com.chema.backend.controller;

import com.chema.backend.dto.admin.AdminUserDto;
import com.chema.backend.dto.admin.AdminUserCreateDto;
import com.chema.backend.dto.admin.AdminUserUpdateDto;
import com.chema.backend.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de gestión de usuarios administradores.
 * Estas rutas estarán protegidas por SecurityConfig: sólo acceden super-admins.
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService service;

    @GetMapping("")
    public ResponseEntity<List<AdminUserDto>> list() {
        return ResponseEntity.ok(service.listAll());
    }

    @PostMapping("")
    public ResponseEntity<AdminUserDto> create(@RequestBody @Validated AdminUserCreateDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminUserDto> update(
            @PathVariable Long id,
            @RequestBody @Validated AdminUserUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
