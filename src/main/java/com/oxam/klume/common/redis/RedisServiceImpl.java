package com.oxam.klume.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@RequiredArgsConstructor
@Component
public class RedisServiceImpl implements RedisService {
    private final StringRedisTemplate redisTemplate;

    public void set(final String key, final String value, final Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public String get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(final String key) {
        redisTemplate.delete(key);
    }
}