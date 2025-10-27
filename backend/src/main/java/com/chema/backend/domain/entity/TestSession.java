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

    @Column(name = "participant_name", nullable = false, length = 150)
    private String participantName;

    @Column(name = "participant_surname", nullable = false, length = 150)
    private String participantSurname;

    @Column(name = "participant_birth_date")
    private java.time.LocalDate participantBirthDate;

    @Column(name = "participant_country", length = 150)
    private String participantCountry;

    @Column(name = "participant_gender", length = 50)
    private String participantGender;

    @Column(name = "participant_dominant_hand", length = 50)
    private String participantDominantHand;

    @Column(name = "participant_position", length = 100)
    private String participantPosition;

    @Column(name = "participant_inasidnr", length = 50)
    private String participantInasidnr;

    @Column(name = "participant_event", length = 150)
    private String participantEvent;

    @Column(name = "participant_instructor", length = 150)
    private String participantInstructor;

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
