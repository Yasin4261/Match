package com.match.adapter.rest.security;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class CurrentUser {
    private CurrentUser() {}
    public static UUID id() {
        Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (p instanceof UUID u) return u;
        return UUID.fromString(p.toString());
    }
}

