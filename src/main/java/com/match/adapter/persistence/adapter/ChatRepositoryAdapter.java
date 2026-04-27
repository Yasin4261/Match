package com.match.adapter.persistence.adapter;

import com.match.adapter.persistence.jpa.ConversationJpaEntity;
import com.match.adapter.persistence.jpa.MessageJpaEntity;
import com.match.adapter.persistence.repository.ConversationSpringDataRepository;
import com.match.adapter.persistence.repository.MessageSpringDataRepository;
import com.match.domain.chat.Conversation;
import com.match.domain.chat.Message;
import com.match.domain.chat.MessageStatus;
import com.match.domain.chat.MessageType;
import com.match.domain.port.out.ChatRepositoryPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ChatRepositoryAdapter implements ChatRepositoryPort {

    private final ConversationSpringDataRepository convRepo;
    private final MessageSpringDataRepository msgRepo;

    public ChatRepositoryAdapter(ConversationSpringDataRepository convRepo, MessageSpringDataRepository msgRepo) {
        this.convRepo = convRepo;
        this.msgRepo = msgRepo;
    }

    @Override public Conversation getOrCreateForMatch(UUID matchId) {
        return convRepo.findByMatchId(matchId).map(this::toConv)
            .orElseGet(() -> {
                ConversationJpaEntity e = ConversationJpaEntity.builder()
                    .id(UUID.randomUUID()).matchId(matchId).lastMessageAt(Instant.now()).build();
                return toConv(convRepo.save(e));
            });
    }

    @Override public Optional<Conversation> findById(UUID conversationId) {
        return convRepo.findById(conversationId).map(this::toConv);
    }

    @Override public Message saveMessage(Message m) {
        MessageJpaEntity e = MessageJpaEntity.builder()
            .id(m.id()).conversationId(m.conversationId()).senderId(m.senderId())
            .body(m.body()).type(m.type().name()).status(m.status().name()).createdAt(m.createdAt()).build();
        MessageJpaEntity saved = msgRepo.save(e);
        convRepo.findById(m.conversationId()).ifPresent(c -> {
            c.setLastMessageAt(m.createdAt());
            convRepo.save(c);
        });
        return toMsg(saved);
    }

    @Override public List<Message> recentMessages(UUID conversationId, int limit) {
        return msgRepo.findByConversationIdOrderByCreatedAtDesc(conversationId, PageRequest.of(0, limit))
            .stream().map(this::toMsg).toList();
    }

    private Conversation toConv(ConversationJpaEntity e) {
        return new Conversation(e.getId(), e.getMatchId(), e.getLastMessageAt());
    }

    private Message toMsg(MessageJpaEntity e) {
        return new Message(e.getId(), e.getConversationId(), e.getSenderId(), e.getBody(),
            MessageType.valueOf(e.getType()), MessageStatus.valueOf(e.getStatus()), e.getCreatedAt());
    }
}

