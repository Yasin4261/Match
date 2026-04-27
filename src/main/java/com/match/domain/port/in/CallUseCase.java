package com.match.domain.port.in;

import com.match.domain.call.CallSession;
import com.match.domain.call.CallType;

import java.util.UUID;

public interface CallUseCase {
    CallSession initiate(UUID callerId, UUID calleeId, CallType type);
    CallSession answer(UUID callId, UUID calleeId);
    CallSession end(UUID callId, UUID actorId, String reason);
    /** Issue ephemeral TURN credentials (HMAC-SHA1, time-limited). */
    TurnCredentials issueTurnCredentials(UUID userId);

    record TurnCredentials(String username, String password, long ttlSeconds, String[] urls) {}
}

