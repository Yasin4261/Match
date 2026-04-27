package com.match.adapter.rest.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class BCryptPasswordHasherAdapterTest {

    private final BCryptPasswordHasherAdapter adapter =
        new BCryptPasswordHasherAdapter(new BCryptPasswordEncoder());

    @Test
    void hash_then_match_succeeds() {
        String h = adapter.hash("Secret123");
        assertNotEquals("Secret123", h);
        assertTrue(adapter.matches("Secret123", h));
    }

    @Test
    void wrong_password_does_not_match() {
        String h = adapter.hash("Secret123");
        assertFalse(adapter.matches("WrongPw", h));
    }

    @Test
    void produces_distinct_hashes_per_call() {
        assertNotEquals(adapter.hash("same"), adapter.hash("same"));
    }
}

