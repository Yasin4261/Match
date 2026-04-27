package com.match.domain.port.out;

import com.match.domain.swipe.Swipe;
import com.match.domain.swipe.SwipeDirection;

import java.util.Optional;
import java.util.UUID;

public interface SwipeRepositoryPort {
    Swipe save(Swipe swipe);
    Optional<Swipe> find(UUID swiperId, UUID swipeeId);
    boolean exists(UUID swiperId, UUID swipeeId, SwipeDirection direction);
}

