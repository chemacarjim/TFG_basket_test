package com.chema.backend.repository;

import com.chema.backend.domain.entity.Question;
import com.chema.backend.domain.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTest(TestEntity test);
}
