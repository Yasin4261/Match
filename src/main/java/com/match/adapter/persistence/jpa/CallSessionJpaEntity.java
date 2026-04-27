package com.match.adapter.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "call_sessions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CallSessionJpaEntity {
    @Id private UUID id;
    @Column(name = "caller_id", nullable = false) private UUID callerId;
    @Column(name = "callee_id", nullable = false) private UUID calleeId;
    @Column(nullable = false) private String type;
    @Column(nullable = false) private String state;
    @Column(nullable = false) private Instant startedAt;
    private Instant answeredAt;
    private Instant endedAt;
    private String endReason;
}

