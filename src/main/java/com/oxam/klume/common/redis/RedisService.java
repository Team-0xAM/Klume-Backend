package com.oxam.klume.common.redis;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface RedisService {
    void set(final String key, final String value, final Duration duration);

    void setDataExpire(final String key, final String value, final long duration, final TimeUnit timeUnit);

    String getData(final String key);

    void deleteData(final String key);
}