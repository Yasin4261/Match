package com.match.domain.port.in;

import com.match.domain.chat.Message;

import java.util.UUID;

public interface ChatUseCase {
    record SendMessageCommand(UUID senderId, UUID conversationId, String body) {}
    Message send(SendMessageCommand cmd);
}

