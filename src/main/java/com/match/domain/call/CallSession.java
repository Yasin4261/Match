package com.match.domain.call;

import java.time.Instant;
import java.util.UUID;

public record CallSession(
    UUID id,
    UUID callerId,
    UUID calleeId,
    CallType type,
    CallState state,
    Instant startedAt,
    Instant answeredAt,
    Instant endedAt,
    String endReason
) {
    public static CallSession initiate(UUID caller, UUID callee, CallType type) {
        return new CallSession(UUID.randomUUID(), caller, callee, type, CallState.RINGING,
            Instant.now(), null, null, null);
    }
    public CallSession answered() {
        return new CallSession(id, callerId, calleeId, type, CallState.IN_CALL, startedAt, Instant.now(), null, null);
    }
    public CallSession ended(String reason) {
        return new CallSession(id, callerId, calleeId, type, CallState.ENDED, startedAt, answeredAt, Instant.now(), reason);
    }
}

