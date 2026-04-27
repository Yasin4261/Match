package com.match.domain.port.out;

import com.match.domain.call.CallSession;

import java.util.Optional;
import java.util.UUID;

public interface CallRepositoryPort {
    CallSession save(CallSession session);
    Optional<CallSession> findById(UUID id);
}

