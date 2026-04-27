package com.match.adapter.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "matches",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_a_id", "user_b_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MatchJpaEntity {
    @Id private UUID id;
    @Column(name = "user_a_id", nullable = false) private UUID userAId;
    @Column(name = "user_b_id", nullable = false) private UUID userBId;
    @Column(nullable = false) private Instant createdAt;
    private Instant unmatchedAt;
}

