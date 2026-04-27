package com.match.adapter.rest.security;

import com.match.domain.port.out.PasswordHasherPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasherAdapter implements PasswordHasherPort {
    private final PasswordEncoder encoder;
    public BCryptPasswordHasherAdapter(PasswordEncoder encoder) { this.encoder = encoder; }
    @Override public String hash(String raw) { return encoder.encode(raw); }
    @Override public boolean matches(String raw, String hash) { return encoder.matches(raw, hash); }
}

