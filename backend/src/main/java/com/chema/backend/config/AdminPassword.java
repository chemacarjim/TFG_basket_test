package com.chema.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminPassword implements ApplicationRunner {
    private final JdbcTemplate jdbc;

    @Override
    public void run(ApplicationArguments args) {
        var encoder = new BCryptPasswordEncoder();
        jdbc.query("SELECT id, password_plain FROM admin_user WHERE password_plain IS NOT NULL",
                (rs) -> {
                    Long id = rs.getLong("id");
                    String plain = rs.getString("password_plain");
                    String hash = encoder.encode(plain);
                    jdbc.update("UPDATE admin_user SET password_hash=?, password_plain=NULL WHERE id=?", hash, id);
                });
    }
}
