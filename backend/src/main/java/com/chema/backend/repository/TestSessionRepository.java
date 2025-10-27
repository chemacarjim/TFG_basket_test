package com.chema.backend.repository;

import com.chema.backend.domain.entity.TestSession;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;

public interface TestSessionRepository extends JpaRepository<TestSession, Long> {

    @Query("""
            select s from TestSession s
            left join fetch s.responses r
            left join fetch r.question q
            left join fetch s.test t
            where s.id = :id
            """)
    Optional<TestSession> findByIdWithResponses(@Param("id") Long id);

    @Query(value = """
            select s.* from test_session s
            where s.test_id = :testId
                and s.finished_at is not null
            order by s.started_at desc
            limit :limit offset :offset
            """,
            nativeQuery = true)
    List<TestSession> findFinishedByTestIdPaged(@Param("testId") Long testId, 
                                                @Param("limit") int limit, 
                                                @Param("offset") int offset);
}
