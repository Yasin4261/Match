package com.match.adapter.rest.call;

import com.match.adapter.rest.security.CurrentUser;
import com.match.domain.call.CallSession;
import com.match.domain.call.CallType;
import com.match.domain.port.in.CallUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/calls")
public class CallController {

    private final CallUseCase calls;

    public CallController(CallUseCase calls) { this.calls = calls; }

    public record InitiateReq(@NotNull UUID calleeId, @NotNull CallType type) {}
    public record EndReq(String reason) {}

    @PostMapping
    public CallSession initiate(@Valid @RequestBody InitiateReq req) {
        return calls.initiate(CurrentUser.id(), req.calleeId(), req.type());
    }

    @PostMapping("/{id}/answer")
    public CallSession answer(@PathVariable UUID id) {
        return calls.answer(id, CurrentUser.id());
    }

    @PostMapping("/{id}/end")
    public CallSession end(@PathVariable UUID id, @RequestBody(required = false) EndReq req) {
        return calls.end(id, CurrentUser.id(), req == null ? "hangup" : req.reason());
    }

    @GetMapping("/turn-credentials")
    public CallUseCase.TurnCredentials turn() {
        return calls.issueTurnCredentials(CurrentUser.id());
    }
}

