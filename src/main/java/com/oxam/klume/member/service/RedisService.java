package com.oxam.klume.member.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {
    void setData(String key, String value);

    void setDataExpire(String key, String value, long duration, TimeUnit timeUnit);

    String getData(String key);

    void deleteData(String key);
}
