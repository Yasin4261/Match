package com.match.adapter.cache.redis;

import com.match.domain.port.out.PresencePort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;

@Component
public class RedisPresenceAdapter implements PresencePort {

    private static final Duration TTL = Duration.ofMinutes(2);
    private final StringRedisTemplate redis;

    public RedisPresenceAdapter(StringRedisTemplate redis) { this.redis = redis; }

    @Override public void markOnline(UUID userId, String sessionId) {
        redis.opsForValue().set(key(userId), sessionId, TTL);
    }

    @Override public void markOffline(UUID userId) { redis.delete(key(userId)); }

    @Override public boolean isOnline(UUID userId) {
        return Boolean.TRUE.equals(redis.hasKey(key(userId)));
    }

    private String key(UUID id) { return "presence:user:" + id; }
}

