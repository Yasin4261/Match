package com.match.application.swipe;

import com.match.domain.match.Match;
import com.match.domain.port.in.SwipeUseCase;
import com.match.domain.port.out.MatchRepositoryPort;
import com.match.domain.port.out.SwipeRepositoryPort;
import com.match.domain.swipe.Swipe;
import com.match.domain.swipe.SwipeDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwipeServiceTest {

    @Mock SwipeRepositoryPort swipes;
    @Mock MatchRepositoryPort matches;
    @InjectMocks SwipeService service;

    private final UUID a = UUID.randomUUID();
    private final UUID b = UUID.randomUUID();

    @Test
    void dislike_never_creates_match() {
        when(swipes.save(any(Swipe.class))).thenAnswer(inv -> inv.getArgument(0));
        SwipeUseCase.SwipeResult r = service.swipe(
            new SwipeUseCase.SwipeCommand(a, b, SwipeDirection.DISLIKE));
        assertFalse(r.matched());
        assertTrue(r.matchId().isEmpty());
        verify(matches, never()).save(any());
    }

    @Test
    void like_without_reciprocal_does_not_match() {
        when(swipes.save(any(Swipe.class))).thenAnswer(inv -> inv.getArgument(0));
        when(swipes.exists(b, a, SwipeDirection.LIKE)).thenReturn(false);
        when(swipes.exists(b, a, SwipeDirection.SUPER_LIKE)).thenReturn(false);

        SwipeUseCase.SwipeResult r = service.swipe(
            new SwipeUseCase.SwipeCommand(a, b, SwipeDirection.LIKE));
        assertFalse(r.matched());
        verify(matches, never()).save(any());
    }

    @Test
    void mutual_like_creates_new_match_when_none_exists() {
        when(swipes.save(any(Swipe.class))).thenAnswer(inv -> inv.getArgument(0));
        when(swipes.exists(b, a, SwipeDirection.LIKE)).thenReturn(true);
        when(matches.findBetween(a, b)).thenReturn(Optional.empty());
        when(matches.save(any(Match.class))).thenAnswer(inv -> inv.getArgument(0));

        SwipeUseCase.SwipeResult r = service.swipe(
            new SwipeUseCase.SwipeCommand(a, b, SwipeDirection.LIKE));
        assertTrue(r.matched());
        assertTrue(r.matchId().isPresent());
        verify(matches).save(any(Match.class));
    }

    @Test
    void super_like_with_reciprocal_like_creates_match() {
        when(swipes.save(any(Swipe.class))).thenAnswer(inv -> inv.getArgument(0));
        when(swipes.exists(b, a, SwipeDirection.LIKE)).thenReturn(true);
        when(matches.findBetween(a, b)).thenReturn(Optional.empty());
        when(matches.save(any(Match.class))).thenAnswer(inv -> inv.getArgument(0));

        SwipeUseCase.SwipeResult r = service.swipe(
            new SwipeUseCase.SwipeCommand(a, b, SwipeDirection.SUPER_LIKE));
        assertTrue(r.matched());
    }

    @Test
    void mutual_like_reuses_existing_match() {
        Match existing = Match.between(a, b);
        when(swipes.save(any(Swipe.class))).thenAnswer(inv -> inv.getArgument(0));
        when(swipes.exists(b, a, SwipeDirection.LIKE)).thenReturn(true);
        when(matches.findBetween(a, b)).thenReturn(Optional.of(existing));

        SwipeUseCase.SwipeResult r = service.swipe(
            new SwipeUseCase.SwipeCommand(a, b, SwipeDirection.LIKE));
        assertTrue(r.matched());
        assertEquals(existing.id(), r.matchId().orElseThrow());
        verify(matches, never()).save(any());
    }
}

