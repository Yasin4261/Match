package com.match.application.swipe;

import com.match.domain.match.Match;
import com.match.domain.port.in.SwipeUseCase;
import com.match.domain.port.out.MatchRepositoryPort;
import com.match.domain.port.out.SwipeRepositoryPort;
import com.match.domain.swipe.Swipe;
import com.match.domain.swipe.SwipeDirection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SwipeService implements SwipeUseCase {

    private final SwipeRepositoryPort swipes;
    private final MatchRepositoryPort matches;

    public SwipeService(SwipeRepositoryPort swipes, MatchRepositoryPort matches) {
        this.swipes = swipes;
        this.matches = matches;
    }

    @Override
    @Transactional
    public SwipeResult swipe(SwipeCommand cmd) {
        Swipe s = Swipe.of(cmd.swiperId(), cmd.swipeeId(), cmd.direction());
        swipes.save(s);

        if (cmd.direction() == SwipeDirection.DISLIKE) {
            return new SwipeResult(false, Optional.empty());
        }

        boolean reciprocated = swipes.exists(cmd.swipeeId(), cmd.swiperId(), SwipeDirection.LIKE)
            || swipes.exists(cmd.swipeeId(), cmd.swiperId(), SwipeDirection.SUPER_LIKE);

        if (!reciprocated) return new SwipeResult(false, Optional.empty());

        Match match = matches.findBetween(cmd.swiperId(), cmd.swipeeId())
            .orElseGet(() -> matches.save(Match.between(cmd.swiperId(), cmd.swipeeId())));
        return new SwipeResult(true, Optional.of(match.id()));
    }
}

