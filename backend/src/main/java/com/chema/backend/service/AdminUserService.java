package com.chema.backend.service;

import com.chema.backend.dto.admin.AdminUserDto;
import com.chema.backend.dto.admin.AdminUserCreateDto;
import com.chema.backend.dto.admin.AdminUserUpdateDto;

import java.util.List;

public interface AdminUserService {
    List<AdminUserDto> listAll();
    AdminUserDto create(AdminUserCreateDto dto);
    AdminUserDto update(Long id, AdminUserUpdateDto dto);
    void delete(Long id);
}
