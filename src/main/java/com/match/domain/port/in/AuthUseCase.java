package com.match.domain.port.in;

import java.util.UUID;

public interface AuthUseCase {
    record RegisterCommand(String email, String password, String displayName) {}
    record LoginCommand(String email, String password) {}
    record AuthResult(UUID userId, String accessToken, String refreshToken) {}

    AuthResult register(RegisterCommand cmd);
    AuthResult login(LoginCommand cmd);
    AuthResult refresh(String refreshToken);
}

