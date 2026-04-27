package com.match.domain.swipe;

import java.time.Instant;
import java.util.UUID;

public record Swipe(UUID id, UUID swiperId, UUID swipeeId, SwipeDirection direction, Instant createdAt) {
    public static Swipe of(UUID swiper, UUID swipee, SwipeDirection dir) {
        return new Swipe(UUID.randomUUID(), swiper, swipee, dir, Instant.now());
    }
}

