package com.match.adapter.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "conversations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConversationJpaEntity {
    @Id private UUID id;
    @Column(name = "match_id", nullable = false, unique = true) private UUID matchId;
    private Instant lastMessageAt;
}

