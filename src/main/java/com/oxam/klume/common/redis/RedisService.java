package com.oxam.klume.common.redis;

import java.time.Duration;

public interface RedisService {
    void set(final String key, final String value, final Duration duration);

    String get(final String key);

    void delete(final String key);
}