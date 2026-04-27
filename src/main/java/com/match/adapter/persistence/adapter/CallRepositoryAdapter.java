package com.match.adapter.persistence.adapter;

import com.match.adapter.persistence.jpa.CallSessionJpaEntity;
import com.match.adapter.persistence.repository.CallSessionSpringDataRepository;
import com.match.domain.call.CallSession;
import com.match.domain.call.CallState;
import com.match.domain.call.CallType;
import com.match.domain.port.out.CallRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CallRepositoryAdapter implements CallRepositoryPort {

    private final CallSessionSpringDataRepository repo;

    public CallRepositoryAdapter(CallSessionSpringDataRepository repo) { this.repo = repo; }

    @Override public CallSession save(CallSession s) {
        CallSessionJpaEntity e = CallSessionJpaEntity.builder()
            .id(s.id()).callerId(s.callerId()).calleeId(s.calleeId())
            .type(s.type().name()).state(s.state().name())
            .startedAt(s.startedAt()).answeredAt(s.answeredAt())
            .endedAt(s.endedAt()).endReason(s.endReason()).build();
        return toDomain(repo.save(e));
    }

    @Override public Optional<CallSession> findById(UUID id) { return repo.findById(id).map(this::toDomain); }

    private CallSession toDomain(CallSessionJpaEntity e) {
        return new CallSession(e.getId(), e.getCallerId(), e.getCalleeId(),
            CallType.valueOf(e.getType()), CallState.valueOf(e.getState()),
            e.getStartedAt(), e.getAnsweredAt(), e.getEndedAt(), e.getEndReason());
    }
}

