package com.chema.backend.security;

import com.chema.backend.repository.AdminUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminUserRepository adminRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var admin = adminRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found"));
        var pwd = admin.getPasswordHash() != null ? admin.getPasswordHash() : "{noop}invalid";
        return User.withUsername(admin.getEmail())
                .password(pwd)
                .roles("ADMIN")
                .build();
    }
}
