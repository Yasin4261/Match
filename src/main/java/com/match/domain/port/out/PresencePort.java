package com.match.domain.port.out;

import java.util.UUID;

public interface PresencePort {
    void markOnline(UUID userId, String sessionId);
    void markOffline(UUID userId);
    boolean isOnline(UUID userId);
}

