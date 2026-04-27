package com.match.adapter.persistence.repository;

import com.match.adapter.persistence.jpa.MessageJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageSpringDataRepository extends JpaRepository<MessageJpaEntity, UUID> {
    List<MessageJpaEntity> findByConversationIdOrderByCreatedAtDesc(UUID conversationId, Pageable pageable);
}

