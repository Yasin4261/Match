package com.match.adapter.persistence.repository;

import com.match.adapter.persistence.jpa.ConversationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConversationSpringDataRepository extends JpaRepository<ConversationJpaEntity, UUID> {
    Optional<ConversationJpaEntity> findByMatchId(UUID matchId);
}

