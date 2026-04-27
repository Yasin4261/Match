package com.match.adapter.rest.auth;

import com.match.domain.port.in.AuthUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthUseCase auth;

    public AuthController(AuthUseCase auth) { this.auth = auth; }

    public record RegisterReq(@Email @NotBlank String email,
                              @NotBlank @Size(min = 8) String password,
                              @NotBlank String displayName) {}
    public record LoginReq(@Email @NotBlank String email, @NotBlank String password) {}
    public record RefreshReq(@NotBlank String refreshToken) {}

    @PostMapping("/register")
    public AuthUseCase.AuthResult register(@Valid @RequestBody RegisterReq req) {
        return auth.register(new AuthUseCase.RegisterCommand(req.email(), req.password(), req.displayName()));
    }

    @PostMapping("/login")
    public AuthUseCase.AuthResult login(@Valid @RequestBody LoginReq req) {
        return auth.login(new AuthUseCase.LoginCommand(req.email(), req.password()));
    }

    @PostMapping("/refresh")
    public AuthUseCase.AuthResult refresh(@Valid @RequestBody RefreshReq req) {
        return auth.refresh(req.refreshToken());
    }
}

