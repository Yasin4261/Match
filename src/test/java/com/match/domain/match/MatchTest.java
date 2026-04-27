package com.match.domain.match;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    @RepeatedTest(20)
    void always_orders_user_ids_ascending() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        Match m = Match.between(a, b);
        assertTrue(m.userAId().compareTo(m.userBId()) < 0);
        assertTrue(m.userAId().equals(a) || m.userAId().equals(b));
        assertTrue(m.userBId().equals(a) || m.userBId().equals(b));
    }

    @Test
    void same_users_swapped_yield_same_pair() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        Match m1 = Match.between(a, b);
        Match m2 = Match.between(b, a);
        assertEquals(m1.userAId(), m2.userAId());
        assertEquals(m1.userBId(), m2.userBId());
    }

    @Test
    void created_at_present_and_unmatched_at_null() {
        Match m = Match.between(UUID.randomUUID(), UUID.randomUUID());
        assertNotNull(m.createdAt());
        assertNull(m.unmatchedAt());
    }
}

