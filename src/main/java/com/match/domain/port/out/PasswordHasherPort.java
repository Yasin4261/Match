package com.match.domain.port.out;

public interface PasswordHasherPort {
    String hash(String raw);
    boolean matches(String raw, String hash);
}

