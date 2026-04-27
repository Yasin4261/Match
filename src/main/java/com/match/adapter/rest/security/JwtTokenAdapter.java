package com.match.adapter.rest.security;

import com.match.domain.port.out.TokenPort;
import com.match.domain.shared.DomainException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenAdapter implements TokenPort {

    @Value("${app.jwt.secret}") private String secret;
    @Value("${app.jwt.access-ttl-minutes:30}") private long accessTtl;
    @Value("${app.jwt.refresh-ttl-days:14}") private long refreshTtlDays;

    private SecretKey key;

    @PostConstruct void init() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("app.jwt.secret must be >= 32 chars");
        }
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override public String issueAccessToken(UUID userId) {
        return build(userId, "access", Duration.ofMinutes(accessTtl));
    }

    @Override public String issueRefreshToken(UUID userId) {
        return build(userId, "refresh", Duration.ofDays(refreshTtlDays));
    }

    @Override public UUID validateAccessToken(String token) { return validate(token, "access"); }

    @Override public UUID validateRefreshToken(String token) { return validate(token, "refresh"); }

    private String build(UUID userId, String type, Duration ttl) {
        Date now = new Date();
        return Jwts.builder()
            .subject(userId.toString())
            .claim("typ", type)
            .issuedAt(now)
            .expiration(new Date(now.getTime() + ttl.toMillis()))
            .signWith(key)
            .compact();
    }

    private UUID validate(String token, String expectedType) {
        try {
            var jws = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            String typ = jws.getPayload().get("typ", String.class);
            if (!expectedType.equals(typ)) throw new DomainException("Wrong token type");
            return UUID.fromString(jws.getPayload().getSubject());
        } catch (DomainException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException("Invalid or expired token");
        }
    }
}

