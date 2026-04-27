package com.match.domain.chat;

import java.time.Instant;
import java.util.UUID;

public record Message(
    UUID id,
    UUID conversationId,
    UUID senderId,
    String body,
    MessageType type,
    MessageStatus status,
    Instant createdAt
) {
    public static Message text(UUID conv, UUID sender, String body) {
        return new Message(UUID.randomUUID(), conv, sender, body, MessageType.TEXT, MessageStatus.SENT, Instant.now());
    }
}

