package com.chema.backend.controller;

import com.chema.backend.dto.LoginRequestDto;
import com.chema.backend.dto.LoginResponseDto;
import com.chema.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import com.chema.backend.domain.entity.AdminUser;
import com.chema.backend.repository.AdminUserRepository;
import com.chema.backend.exception.NotFoundException;

@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final AdminUserRepository adminRepo;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto req) {
        try {
            var token = new UsernamePasswordAuthenticationToken(req.email(), req.password());
            authManager.authenticate(token);
            String jwt = jwtUtil.generateToken(req.email());
            AdminUser admin = adminRepo.findByEmail(req.email())
                .orElseThrow(() -> new NotFoundException("ADMIN_NOT_FOUND", "Admin no encontrado"));
            return ResponseEntity.ok(new LoginResponseDto(jwt, admin.getIsSuperAdmin()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).build();
        }
    }
}
