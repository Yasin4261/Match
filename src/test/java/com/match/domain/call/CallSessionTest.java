package com.match.domain.call;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CallSessionTest {

    @Test
    void initiate_starts_in_ringing_state() {
        CallSession s = CallSession.initiate(UUID.randomUUID(), UUID.randomUUID(), CallType.VIDEO);
        assertEquals(CallState.RINGING, s.state());
        assertNotNull(s.startedAt());
        assertNull(s.answeredAt());
        assertNull(s.endedAt());
    }

    @Test
    void answered_transitions_to_in_call_and_sets_answeredAt() {
        CallSession s = CallSession.initiate(UUID.randomUUID(), UUID.randomUUID(), CallType.VOICE).answered();
        assertEquals(CallState.IN_CALL, s.state());
        assertNotNull(s.answeredAt());
    }

    @Test
    void ended_sets_state_endedAt_and_reason() {
        CallSession s = CallSession.initiate(UUID.randomUUID(), UUID.randomUUID(), CallType.VIDEO)
            .answered().ended("hangup");
        assertEquals(CallState.ENDED, s.state());
        assertNotNull(s.endedAt());
        assertEquals("hangup", s.endReason());
    }
}

