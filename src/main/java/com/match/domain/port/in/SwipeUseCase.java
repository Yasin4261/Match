package com.match.domain.port.in;

import com.match.domain.swipe.SwipeDirection;

import java.util.Optional;
import java.util.UUID;

public interface SwipeUseCase {
    record SwipeCommand(UUID swiperId, UUID swipeeId, SwipeDirection direction) {}
    record SwipeResult(boolean matched, Optional<UUID> matchId) {}
    SwipeResult swipe(SwipeCommand cmd);
}

