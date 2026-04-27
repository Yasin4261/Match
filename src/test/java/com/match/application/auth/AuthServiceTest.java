package com.match.application.auth;

import com.match.domain.port.in.AuthUseCase;
import com.match.domain.port.out.PasswordHasherPort;
import com.match.domain.port.out.TokenPort;
import com.match.domain.port.out.UserRepositoryPort;
import com.match.domain.shared.DomainException;
import com.match.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepositoryPort users;
    @Mock PasswordHasherPort hasher;
    @Mock TokenPort tokens;

    @InjectMocks AuthService service;

    @BeforeEach
    void stubTokens() {
        lenient().when(tokens.issueAccessToken(any())).thenReturn("access");
        lenient().when(tokens.issueRefreshToken(any())).thenReturn("refresh");
    }

    @Test
    void register_persists_user_with_hashed_password_and_returns_tokens() {
        when(users.existsByEmail("a@b.com")).thenReturn(false);
        when(hasher.hash("Secret123")).thenReturn("HASH");
        when(users.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthUseCase.AuthResult result = service.register(
            new AuthUseCase.RegisterCommand("A@B.com", "Secret123", "Ada"));

        assertEquals("access", result.accessToken());
        assertEquals("refresh", result.refreshToken());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(users).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("a@b.com", saved.email(), "email must be lowercased");
        assertEquals("HASH", saved.passwordHash());
    }

    @Test
    void register_rejects_duplicate_email() {
        when(users.existsByEmail("dup@x.com")).thenReturn(true);
        assertThrows(DomainException.class, () -> service.register(
            new AuthUseCase.RegisterCommand("dup@x.com", "Secret123", "X")));
        verify(users, never()).save(any());
    }

    @Test
    void login_returns_tokens_when_password_matches() {
        UUID id = UUID.randomUUID();
        User existing = new User(id, "x@y.com", "HASH",
            com.match.domain.user.UserStatus.ACTIVE, java.time.Instant.now());
        when(users.findByEmail("x@y.com")).thenReturn(Optional.of(existing));
        when(hasher.matches("pw", "HASH")).thenReturn(true);

        AuthUseCase.AuthResult result = service.login(new AuthUseCase.LoginCommand("X@Y.com", "pw"));
        assertEquals(id, result.userId());
    }

    @Test
    void login_throws_when_user_missing() {
        when(users.findByEmail(any())).thenReturn(Optional.empty());
        assertThrows(DomainException.class, () -> service.login(new AuthUseCase.LoginCommand("a@b", "pw")));
    }

    @Test
    void login_throws_when_password_does_not_match() {
        User existing = new User(UUID.randomUUID(), "x@y.com", "HASH",
            com.match.domain.user.UserStatus.ACTIVE, java.time.Instant.now());
        when(users.findByEmail("x@y.com")).thenReturn(Optional.of(existing));
        when(hasher.matches(any(), any())).thenReturn(false);
        assertThrows(DomainException.class, () -> service.login(new AuthUseCase.LoginCommand("x@y.com", "bad")));
    }

    @Test
    void refresh_validates_token_and_issues_new_pair() {
        UUID id = UUID.randomUUID();
        when(tokens.validateRefreshToken("rt")).thenReturn(id);
        AuthUseCase.AuthResult r = service.refresh("rt");
        assertEquals(id, r.userId());
        verify(tokens).issueAccessToken(eq(id));
        verify(tokens).issueRefreshToken(eq(id));
    }
}

