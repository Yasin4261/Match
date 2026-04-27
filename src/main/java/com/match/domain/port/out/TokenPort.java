package com.match.domain.port.out;

import java.util.UUID;

public interface TokenPort {
    String issueAccessToken(UUID userId);
    String issueRefreshToken(UUID userId);
    UUID validateAccessToken(String token);
    UUID validateRefreshToken(String token);
}

