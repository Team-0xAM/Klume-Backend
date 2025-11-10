package com.oxam.klume.common.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class RedisServiceImpl implements RedisService {
    private final StringRedisTemplate redisTemplate;

    @Override
    public void set(final String key, final String value, final Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    @Override
    public void setDataExpire(final String key, final String value, final long duration, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, duration, timeUnit);
    }

    @Override
    public String getData(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteData(final String key) {
        redisTemplate.delete(key);
    }
}