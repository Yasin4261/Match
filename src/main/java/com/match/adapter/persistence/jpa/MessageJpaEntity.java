package com.match.adapter.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "messages",
    indexes = @Index(name = "idx_messages_conv_created", columnList = "conversation_id, createdAt"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MessageJpaEntity {
    @Id private UUID id;
    @Column(name = "conversation_id", nullable = false) private UUID conversationId;
    @Column(name = "sender_id", nullable = false) private UUID senderId;
    @Column(nullable = false, length = 4000) private String body;
    @Column(nullable = false) private String type;
    @Column(nullable = false) private String status;
    @Column(nullable = false) private Instant createdAt;
}

