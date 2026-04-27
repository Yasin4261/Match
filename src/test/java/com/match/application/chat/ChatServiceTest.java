package com.match.application.chat;

import com.match.domain.chat.Conversation;
import com.match.domain.chat.Message;
import com.match.domain.port.in.ChatUseCase;
import com.match.domain.port.out.ChatRepositoryPort;
import com.match.domain.port.out.SignalingPort;
import com.match.domain.shared.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock ChatRepositoryPort repo;
    @Mock SignalingPort signaling;
    @InjectMocks ChatService service;

    @Test
    void send_persists_message_when_conversation_exists() {
        UUID conv = UUID.randomUUID();
        UUID sender = UUID.randomUUID();
        when(repo.findById(conv)).thenReturn(Optional.of(new Conversation(conv, UUID.randomUUID(), Instant.now())));
        when(repo.saveMessage(any(Message.class))).thenAnswer(inv -> inv.getArgument(0));

        Message saved = service.send(new ChatUseCase.SendMessageCommand(sender, conv, "hi"));
        assertEquals("hi", saved.body());
        assertEquals(sender, saved.senderId());
        assertEquals(conv, saved.conversationId());
    }

    @Test
    void send_throws_when_conversation_missing() {
        when(repo.findById(any())).thenReturn(Optional.empty());
        assertThrows(DomainException.class, () -> service.send(
            new ChatUseCase.SendMessageCommand(UUID.randomUUID(), UUID.randomUUID(), "x")));
    }
}

