package com.match.integration;

import com.match.domain.port.in.SwipeUseCase;
import com.match.domain.port.out.UserRepositoryPort;
import com.match.domain.swipe.SwipeDirection;
import com.match.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SwipeMatchFlowIT extends AbstractPostgresIT {

    @Autowired UserRepositoryPort users;
    @Autowired SwipeUseCase swipe;

    @Test
    void mutual_like_creates_match_in_database() {
        User a = users.save(User.newUser("a-" + UUID.randomUUID() + "@x.com", "h"));
        User b = users.save(User.newUser("b-" + UUID.randomUUID() + "@x.com", "h"));

        SwipeUseCase.SwipeResult r1 =
            swipe.swipe(new SwipeUseCase.SwipeCommand(a.id(), b.id(), SwipeDirection.LIKE));
        assertFalse(r1.matched());

        SwipeUseCase.SwipeResult r2 =
            swipe.swipe(new SwipeUseCase.SwipeCommand(b.id(), a.id(), SwipeDirection.LIKE));
        assertTrue(r2.matched(), "second LIKE must produce a match");
        assertTrue(r2.matchId().isPresent());
    }

    @Test
    void dislike_then_like_does_not_match() {
        User a = users.save(User.newUser("c-" + UUID.randomUUID() + "@x.com", "h"));
        User b = users.save(User.newUser("d-" + UUID.randomUUID() + "@x.com", "h"));

        swipe.swipe(new SwipeUseCase.SwipeCommand(a.id(), b.id(), SwipeDirection.DISLIKE));
        SwipeUseCase.SwipeResult r =
            swipe.swipe(new SwipeUseCase.SwipeCommand(b.id(), a.id(), SwipeDirection.LIKE));
        assertFalse(r.matched());
    }
}

