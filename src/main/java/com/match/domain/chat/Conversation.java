package com.match.domain.chat;

import java.time.Instant;
import java.util.UUID;

public record Conversation(UUID id, UUID matchId, Instant lastMessageAt) { }

