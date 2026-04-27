package com.match.application.chat;

import com.match.domain.chat.Conversation;
import com.match.domain.chat.Message;
import com.match.domain.port.in.ChatUseCase;
import com.match.domain.port.out.ChatRepositoryPort;
import com.match.domain.port.out.SignalingPort;
import com.match.domain.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatService implements ChatUseCase {

    private final ChatRepositoryPort repo;
    private final SignalingPort signaling;

    public ChatService(ChatRepositoryPort repo, SignalingPort signaling) {
        this.repo = repo;
        this.signaling = signaling;
    }

    @Override
    @Transactional
    public Message send(SendMessageCommand cmd) {
        Conversation conv = repo.findById(cmd.conversationId())
            .orElseThrow(() -> new DomainException("Conversation not found"));
        Message saved = repo.saveMessage(Message.text(conv.id(), cmd.senderId(), cmd.body()));
        // Note: conversation participants resolution & broadcast handled in adapter layer (STOMP topic).
        return saved;
    }
}

