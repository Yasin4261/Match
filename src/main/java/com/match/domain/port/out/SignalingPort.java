package com.match.domain.port.out;

import java.util.UUID;

/** Outbound port for delivering signaling/chat events to a specific user (WS). */
public interface SignalingPort {
    void sendToUser(UUID userId, String payload);
}

