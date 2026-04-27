package com.match.domain.match;

import java.time.Instant;
import java.util.UUID;

public record Match(UUID id, UUID userAId, UUID userBId, Instant createdAt, Instant unmatchedAt) {
    /** Always store with userAId &lt; userBId for uniqueness. */
    public static Match between(UUID a, UUID b) {
        UUID lo = a.compareTo(b) < 0 ? a : b;
        UUID hi = a.compareTo(b) < 0 ? b : a;
        return new Match(UUID.randomUUID(), lo, hi, Instant.now(), null);
    }
}

