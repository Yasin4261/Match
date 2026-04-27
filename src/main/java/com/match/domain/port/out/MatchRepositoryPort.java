package com.match.domain.port.out;

import com.match.domain.match.Match;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchRepositoryPort {
    Match save(Match match);
    Optional<Match> findBetween(UUID a, UUID b);
    Optional<Match> findById(UUID id);
    List<Match> findAllForUser(UUID userId);
}

