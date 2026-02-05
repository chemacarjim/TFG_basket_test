package com.chema.backend.service.impl;

import com.chema.backend.domain.entity.AdminUser;
import com.chema.backend.dto.admin.AdminUserDto;
import com.chema.backend.dto.admin.AdminUserCreateDto;
import com.chema.backend.dto.admin.AdminUserUpdateDto;
import com.chema.backend.exception.BadRequestException;
import com.chema.backend.exception.NotFoundException;
import com.chema.backend.repository.AdminUserRepository;
import com.chema.backend.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository adminRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<AdminUserDto> listAll() {
        return adminRepo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public AdminUserDto create(AdminUserCreateDto dto) {
        if (adminRepo.existsByEmail(dto.email())) {
            throw new BadRequestException("EMAIL_IN_USE", "Ya existe un administrador con ese email");
        }
        AdminUser user = AdminUser.builder()
                .email(dto.email())
                .passwordHash(passwordEncoder.encode(dto.password()))
                .isSuperAdmin(dto.isSuperAdmin())
                .build();
        adminRepo.save(user);
        return toDto(user);
    }

    @Override
    public AdminUserDto update(Long id, AdminUserUpdateDto dto) {
        AdminUser user = adminRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("ADMIN_NOT_FOUND", "Admin no encontrado"));
        if (dto.email() != null) {
            user.setEmail(dto.email());
        }
        if (dto.password() != null) {
            user.setPasswordHash(passwordEncoder.encode(dto.password()));
        }
        if (dto.isSuperAdmin() != null) {
            user.setIsSuperAdmin(dto.isSuperAdmin());
        }
        adminRepo.save(user);
        return toDto(user);
    }

    @Override
    public void delete(Long id) {
        AdminUser user = adminRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("ADMIN_NOT_FOUND", "Admin no encontrado"));
        // Evitar borrar al último super-admin
        if (user.getIsSuperAdmin() != null && user.getIsSuperAdmin()) {
            long superCount = adminRepo.countByIsSuperAdminTrue();
            if (superCount <= 1) {
                throw new BadRequestException("LAST_SUPER_ADMIN", "No puedes eliminar el último super-admin");
            }
        }
        adminRepo.delete(user);
    }

    private AdminUserDto toDto(AdminUser u) {
        return new AdminUserDto(
            u.getId(),
            u.getEmail(),
            u.getCreatedAt(),
            u.getLastLoginAt(),
            u.getIsSuperAdmin()
        );
    }
}
