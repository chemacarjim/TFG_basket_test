package com.chema.backend.repository;

import com.chema.backend.domain.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<TestEntity, Long> {
    List<TestEntity> findByIsActiveTrueOrderByCreatedAtDesc();
}
