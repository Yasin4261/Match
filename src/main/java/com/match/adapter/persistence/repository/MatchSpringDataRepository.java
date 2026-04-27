package com.match.adapter.persistence.repository;

import com.match.adapter.persistence.jpa.MatchJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchSpringDataRepository extends JpaRepository<MatchJpaEntity, UUID> {
    Optional<MatchJpaEntity> findByUserAIdAndUserBId(UUID a, UUID b);
    List<MatchJpaEntity> findByUserAIdOrUserBId(UUID a, UUID b);
}

