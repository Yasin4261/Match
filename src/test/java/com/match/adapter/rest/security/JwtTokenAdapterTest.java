package com.match.adapter.rest.security;

import com.match.domain.shared.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenAdapterTest {

    private JwtTokenAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new JwtTokenAdapter();
        ReflectionTestUtils.setField(adapter, "secret",
            "test-test-test-test-test-test-test-test-secret-32chars");
        ReflectionTestUtils.setField(adapter, "accessTtl", 5L);
        ReflectionTestUtils.setField(adapter, "refreshTtlDays", 1L);
        ReflectionTestUtils.invokeMethod(adapter, "init");
    }

    @Test
    void access_token_round_trip() {
        UUID id = UUID.randomUUID();
        String token = adapter.issueAccessToken(id);
        assertEquals(id, adapter.validateAccessToken(token));
    }

    @Test
    void refresh_token_round_trip() {
        UUID id = UUID.randomUUID();
        String token = adapter.issueRefreshToken(id);
        assertEquals(id, adapter.validateRefreshToken(token));
    }

    @Test
    void access_token_rejected_when_validated_as_refresh() {
        String access = adapter.issueAccessToken(UUID.randomUUID());
        assertThrows(DomainException.class, () -> adapter.validateRefreshToken(access));
    }

    @Test
    void refresh_token_rejected_when_validated_as_access() {
        String refresh = adapter.issueRefreshToken(UUID.randomUUID());
        assertThrows(DomainException.class, () -> adapter.validateAccessToken(refresh));
    }

    @Test
    void tampered_token_is_rejected() {
        String token = adapter.issueAccessToken(UUID.randomUUID());
        // Flip a character inside the signature segment to break HMAC verification
        int lastDot = token.lastIndexOf('.');
        char ch = token.charAt(lastDot + 1);
        char flipped = ch == 'A' ? 'B' : 'A';
        String tampered = token.substring(0, lastDot + 1) + flipped + token.substring(lastDot + 2);
        assertThrows(DomainException.class, () -> adapter.validateAccessToken(tampered));
    }

    @Test
    void garbage_token_is_rejected() {
        assertThrows(DomainException.class, () -> adapter.validateAccessToken("not-a-jwt"));
    }

    @Test
    void short_secret_is_rejected_at_init() {
        JwtTokenAdapter bad = new JwtTokenAdapter();
        ReflectionTestUtils.setField(bad, "secret", "tooShort");
        assertThrows(IllegalStateException.class, () -> ReflectionTestUtils.invokeMethod(bad, "init"));
    }
}


