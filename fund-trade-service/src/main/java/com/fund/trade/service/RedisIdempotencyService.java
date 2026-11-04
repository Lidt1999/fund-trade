package com.fund.trade.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisIdempotencyService implements IdempotencyService {
    private static final Duration KEY_TTL = Duration.ofMinutes(30);

    private final StringRedisTemplate redis;

    public RedisIdempotencyService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public boolean tryAcquire(String key) {
        Boolean acquired = redis.opsForValue().setIfAbsent(key, "1", KEY_TTL);
        return Boolean.TRUE.equals(acquired);
    }

    @Override
    public void release(String key) {
        redis.delete(key);
    }
}
