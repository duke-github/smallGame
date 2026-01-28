package com.ccw.base.redis;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> template;

    public Object getByKey(String key) {
        return template.opsForValue().get(key);
    }

    public HashOperations<String, Object, Object> getHashOpe() {
        return template.opsForHash();
    }

    public ZSetOperations<String, Object> getZSetOpe() {
        return template.opsForZSet();
    }

    public RedisTemplate<String, Object> getTemplate() {
        return template;
    }
}
