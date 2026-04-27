package com.match.bootstrap;

import com.match.domain.match.Match;
import com.match.domain.swipe.Swipe;
import com.match.domain.swipe.SwipeDirection;
import com.match.domain.user.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/** Pure-domain unit tests; no Spring context needed. */
class DomainSmokeTest {

    @Test
    void user_factory_creates_active_user() {
        User u = User.newUser("a@b.com", "hash");
        assertNotNull(u.id());
        assertEquals("a@b.com", u.email());
    }

    @Test
    void match_orders_user_ids() {
        UUID a = UUID.randomUUID();
        UUID b = UUID.randomUUID();
        Match m = Match.between(a, b);
        assertTrue(m.userAId().compareTo(m.userBId()) < 0);
    }

    @Test
    void swipe_factory_assigns_direction() {
        Swipe s = Swipe.of(UUID.randomUUID(), UUID.randomUUID(), SwipeDirection.LIKE);
        assertEquals(SwipeDirection.LIKE, s.direction());
    }
}

