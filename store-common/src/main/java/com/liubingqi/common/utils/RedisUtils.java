package com.liubingqi.common.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final StringRedisTemplate stringRedisTemplate;

    public void set(String key, Object value) {
        stringRedisTemplate.opsForValue().set(key, toJson(value));
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, toJson(value), timeout, unit);
    }

    public Boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, toJson(value), timeout, unit);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public <T> T get(String key, Class<T> clazz) {
        String json = get(key);
        if (StrUtil.isBlank(json)) {
            return null;
        }
        if (clazz == String.class) {
            return clazz.cast(json);
        }
        return JSON.parseObject(json, clazz);
    }

    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    public Long delete(Collection<String> keys) {
        return stringRedisTemplate.delete(keys);
    }

    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return stringRedisTemplate.expire(key, timeout, unit);
    }

    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    public Long increment(String key, long delta) {
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    public Long decrement(String key) {
        return stringRedisTemplate.opsForValue().decrement(key);
    }

    public Long decrement(String key, long delta) {
        return stringRedisTemplate.opsForValue().decrement(key, delta);
    }

    private String toJson(Object value) {
        if (value instanceof String str) {
            return str;
        }
        return JSON.toJSONString(value);
    }
}
