package com.match.application.call;

import com.match.domain.call.CallSession;
import com.match.domain.call.CallState;
import com.match.domain.call.CallType;
import com.match.domain.port.in.CallUseCase;
import com.match.domain.port.out.CallRepositoryPort;
import com.match.domain.port.out.PresencePort;
import com.match.domain.port.out.SignalingPort;
import com.match.domain.shared.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CallServiceTest {

    @Mock CallRepositoryPort calls;
    @Mock PresencePort presence;
    @Mock SignalingPort signaling;
    @InjectMocks CallService service;

    private final UUID caller = UUID.randomUUID();
    private final UUID callee = UUID.randomUUID();

    @BeforeEach
    void initFields() {
        ReflectionTestUtils.setField(service, "turnSecret", "test-secret");
        ReflectionTestUtils.setField(service, "turnUrlsCsv", "turn:host:3478?transport=udp,turn:host:3478?transport=tcp");
        ReflectionTestUtils.setField(service, "turnTtl", 60L);
    }

    @Test
    void initiate_to_offline_callee_immediately_ends_session() {
        when(presence.isOnline(callee)).thenReturn(false);
        when(calls.save(any(CallSession.class))).thenAnswer(inv -> inv.getArgument(0));

        CallSession s = service.initiate(caller, callee, CallType.VIDEO);
        assertEquals(CallState.ENDED, s.state());
        assertEquals("callee_offline", s.endReason());
        verify(signaling, never()).sendToUser(any(), any());
    }

    @Test
    void initiate_to_online_callee_creates_ringing_and_signals_invite() {
        when(presence.isOnline(callee)).thenReturn(true);
        when(calls.save(any(CallSession.class))).thenAnswer(inv -> inv.getArgument(0));

        CallSession s = service.initiate(caller, callee, CallType.VOICE);
        assertEquals(CallState.RINGING, s.state());
        verify(signaling).sendToUser(eq(callee), contains("call-invite"));
    }

    @Test
    void answer_must_be_invoked_by_callee() {
        UUID id = UUID.randomUUID();
        CallSession ringing = new CallSession(id, caller, callee, CallType.VIDEO, CallState.RINGING,
            java.time.Instant.now(), null, null, null);
        when(calls.findById(id)).thenReturn(Optional.of(ringing));

        assertThrows(DomainException.class, () -> service.answer(id, UUID.randomUUID()));
        verify(calls, never()).save(any());
    }

    @Test
    void answer_transitions_session_and_signals_caller() {
        UUID id = UUID.randomUUID();
        CallSession ringing = new CallSession(id, caller, callee, CallType.VIDEO, CallState.RINGING,
            java.time.Instant.now(), null, null, null);
        when(calls.findById(id)).thenReturn(Optional.of(ringing));
        when(calls.save(any(CallSession.class))).thenAnswer(inv -> inv.getArgument(0));

        CallSession answered = service.answer(id, callee);
        assertEquals(CallState.IN_CALL, answered.state());
        verify(signaling).sendToUser(eq(caller), contains("call-accept"));
    }

    @Test
    void end_signals_the_other_party_with_reason() {
        UUID id = UUID.randomUUID();
        CallSession in = new CallSession(id, caller, callee, CallType.VIDEO, CallState.IN_CALL,
            java.time.Instant.now(), java.time.Instant.now(), null, null);
        when(calls.findById(id)).thenReturn(Optional.of(in));
        when(calls.save(any(CallSession.class))).thenAnswer(inv -> inv.getArgument(0));

        CallSession ended = service.end(id, caller, "hangup");
        assertEquals(CallState.ENDED, ended.state());
        verify(signaling).sendToUser(eq(callee), contains("hangup"));
    }

    @Test
    void turn_credentials_are_well_formed_and_deterministic_per_username() {
        CallUseCase.TurnCredentials c = service.issueTurnCredentials(UUID.randomUUID());
        assertNotNull(c.username());
        assertTrue(c.username().contains(":"));
        assertNotNull(c.password());
        assertEquals(60L, c.ttlSeconds());
        assertEquals(2, c.urls().length);
    }
}

