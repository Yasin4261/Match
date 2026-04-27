package com.match.adapter.rest.swipe;

import com.match.adapter.rest.security.CurrentUser;
import com.match.domain.port.in.SwipeUseCase;
import com.match.domain.swipe.SwipeDirection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/swipes")
public class SwipeController {

    private final SwipeUseCase swipes;

    public SwipeController(SwipeUseCase swipes) { this.swipes = swipes; }

    public record SwipeReq(@NotNull UUID swipeeId, @NotNull SwipeDirection direction) {}

    @PostMapping
    public SwipeUseCase.SwipeResult swipe(@Valid @RequestBody SwipeReq req) {
        return swipes.swipe(new SwipeUseCase.SwipeCommand(CurrentUser.id(), req.swipeeId(), req.direction()));
    }
}

