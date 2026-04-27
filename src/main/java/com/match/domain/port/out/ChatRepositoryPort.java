package com.match.domain.port.out;

import com.match.domain.chat.Conversation;
import com.match.domain.chat.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepositoryPort {
    Conversation getOrCreateForMatch(UUID matchId);
    Optional<Conversation> findById(UUID conversationId);
    Message saveMessage(Message message);
    List<Message> recentMessages(UUID conversationId, int limit);
}

