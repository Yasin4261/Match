package com.match.application.auth;

import com.match.domain.port.in.AuthUseCase;
import com.match.domain.port.out.PasswordHasherPort;
import com.match.domain.port.out.TokenPort;
import com.match.domain.port.out.UserRepositoryPort;
import com.match.domain.shared.DomainException;
import com.match.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort users;
    private final PasswordHasherPort hasher;
    private final TokenPort tokens;

    public AuthService(UserRepositoryPort users, PasswordHasherPort hasher, TokenPort tokens) {
        this.users = users;
        this.hasher = hasher;
        this.tokens = tokens;
    }

    @Override
    @Transactional
    public AuthResult register(RegisterCommand cmd) {
        String normalizedEmail = cmd.email().toLowerCase();
        if (users.existsByEmail(normalizedEmail)) {
            throw new DomainException("Email already registered");
        }
        User user = User.newUser(normalizedEmail, hasher.hash(cmd.password()));
        User saved = users.save(user);
        return issue(saved.id());
    }

    @Override
    public AuthResult login(LoginCommand cmd) {
        User user = users.findByEmail(cmd.email().toLowerCase())
            .orElseThrow(() -> new DomainException("Invalid credentials"));
        if (!hasher.matches(cmd.password(), user.passwordHash())) {
            throw new DomainException("Invalid credentials");
        }
        return issue(user.id());
    }

    @Override
    public AuthResult refresh(String refreshToken) {
        UUID userId = tokens.validateRefreshToken(refreshToken);
        return issue(userId);
    }

    private AuthResult issue(UUID userId) {
        return new AuthResult(userId, tokens.issueAccessToken(userId), tokens.issueRefreshToken(userId));
    }
}

