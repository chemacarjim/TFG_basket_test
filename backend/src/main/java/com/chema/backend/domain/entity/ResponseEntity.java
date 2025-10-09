package com.chema.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "response")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK session_id ON DELETE CASCADE
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private TestSession session;

    // FK question_id ON DELETE CASCADE
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // Postgres enum choice_value → guardamos como STRING
    @Enumerated(EnumType.STRING)
    @Column(name = "selected_value", nullable = false, length = 16)
    private ChoiceValue selectedValue;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;
}
