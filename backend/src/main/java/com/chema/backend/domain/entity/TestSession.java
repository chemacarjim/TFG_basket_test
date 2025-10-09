package com.chema.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "test_session")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TestSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK test_id ON DELETE CASCADE
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "test_id", nullable = false)
    private TestEntity test;

    @Column(name = "anon_user_code")
    private String anonUserCode;

    @CreationTimestamp
    @Column(name = "started_at", nullable = false, updatable = false)
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    private Integer score;
    private Integer total;

    @Column(name = "duration_ms")
    private Long durationMs;

    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY)
    private List<ResponseEntity> responses;
}
