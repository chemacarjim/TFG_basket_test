package com.chema.backend.repository;

import com.chema.backend.domain.entity.TestEntity;
import com.chema.backend.domain.entity.TestSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestSessionRepository extends JpaRepository<TestSession, Long> {
    List<TestSession> findByTestOrderByStartedAtDesc(TestEntity test);
}
