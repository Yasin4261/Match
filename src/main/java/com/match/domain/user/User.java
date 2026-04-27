package com.match.domain.user;

import java.time.Instant;
import java.util.UUID;

public record User(
    UUID id,
    String email,
    String passwordHash,
    UserStatus status,
    Instant createdAt
) {
    public static User newUser(String email, String passwordHash) {
        return new User(UUID.randomUUID(), email, passwordHash, UserStatus.ACTIVE, Instant.now());
    }
}

