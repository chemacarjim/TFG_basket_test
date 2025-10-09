package com.chema.backend.repository;

import com.chema.backend.domain.entity.ResponseEntity;
import com.chema.backend.domain.entity.TestSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResponseRepository extends JpaRepository<ResponseEntity, Long> {
    List<ResponseEntity> findBySession(TestSession session);
}
