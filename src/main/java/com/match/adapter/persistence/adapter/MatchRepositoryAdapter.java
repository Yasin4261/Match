package com.match.adapter.persistence.adapter;

import com.match.adapter.persistence.jpa.MatchJpaEntity;
import com.match.adapter.persistence.repository.MatchSpringDataRepository;
import com.match.domain.match.Match;
import com.match.domain.port.out.MatchRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class MatchRepositoryAdapter implements MatchRepositoryPort {

    private final MatchSpringDataRepository repo;

    public MatchRepositoryAdapter(MatchSpringDataRepository repo) { this.repo = repo; }

    @Override public Match save(Match m) {
        MatchJpaEntity e = MatchJpaEntity.builder()
            .id(m.id()).userAId(m.userAId()).userBId(m.userBId())
            .createdAt(m.createdAt()).unmatchedAt(m.unmatchedAt()).build();
        return toDomain(repo.save(e));
    }

    @Override public Optional<Match> findBetween(UUID a, UUID b) {
        UUID lo = a.compareTo(b) < 0 ? a : b;
        UUID hi = a.compareTo(b) < 0 ? b : a;
        return repo.findByUserAIdAndUserBId(lo, hi).map(this::toDomain);
    }

    @Override public Optional<Match> findById(UUID id) { return repo.findById(id).map(this::toDomain); }

    @Override public List<Match> findAllForUser(UUID userId) {
        return repo.findByUserAIdOrUserBId(userId, userId).stream().map(this::toDomain).toList();
    }

    private Match toDomain(MatchJpaEntity e) {
        return new Match(e.getId(), e.getUserAId(), e.getUserBId(), e.getCreatedAt(), e.getUnmatchedAt());
    }
}

